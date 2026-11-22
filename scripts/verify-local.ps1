# Full local verification of the Offers API (same steps as the GitHub Actions workflow):
#   1. mvn clean verify  - unit, MockMvc and HTTP integration tests + JaCoCo report
#   2. start the packaged JAR with the in-memory H2 profile
#   3. run the Postman collection with Newman (JUnit + JSON reports in target/)
#   4. stop the application
# Usage (from the project root):  .\scripts\verify-local.ps1
# Exit code is non-zero when any step fails.

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$port = 8080
$baseUrl = "http://localhost:$port"
$jar = 'target/demo-0.0.1-SNAPSHOT.jar'

# Prefer a locally installed Maven, fall back to the Maven Wrapper
$mvn = if (Get-Command mvn -ErrorAction SilentlyContinue) { 'mvn' } else { '.\mvnw.cmd' }

Write-Host "== 1/4 Maven verify ($mvn)" -ForegroundColor Cyan
& $mvn -B --no-transfer-progress clean verify
if ($LASTEXITCODE -ne 0) { throw "Maven verify failed (exit $LASTEXITCODE)" }

Write-Host "== 2/4 Starting $jar with profile h2 on port $port" -ForegroundColor Cyan
$app = Start-Process -FilePath 'java' `
    -ArgumentList '-jar', $jar, '--spring.profiles.active=h2', "--server.port=$port" `
    -RedirectStandardOutput 'target/newman-app.log' -RedirectStandardError 'target/newman-app.err.log' `
    -PassThru -NoNewWindow

try {
    $up = $false
    for ($i = 0; $i -lt 60; $i++) {
        try {
            Invoke-WebRequest -Uri "$baseUrl/v3/api-docs" -UseBasicParsing -TimeoutSec 2 | Out-Null
            $up = $true; break
        } catch { Start-Sleep -Seconds 1 }
    }
    if (-not $up) { throw "Application did not start, see target/newman-app.log" }

    Write-Host "== 3/4 Newman" -ForegroundColor Cyan
    npx --yes newman@6.2.1 run postman/offers-api.postman_collection.json `
        -e postman/local.postman_environment.json --env-var "baseUrl=$baseUrl" `
        --reporters cli,junit,json `
        --reporter-junit-export target/newman-report.xml `
        --reporter-json-export target/newman-report.json
    if ($LASTEXITCODE -ne 0) { throw "Newman reported failures (exit $LASTEXITCODE)" }
}
finally {
    Write-Host "== 4/4 Stopping application" -ForegroundColor Cyan
    if ($app -and -not $app.HasExited) { Stop-Process -Id $app.Id -Force }
}

Write-Host "Verification passed. Reports: target/surefire-reports, target/site/jacoco, target/newman-report.*" -ForegroundColor Green
