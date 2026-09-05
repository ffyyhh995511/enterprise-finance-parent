param(
    [string]$MysqlBin = "mysql",
    [string]$Host = "localhost",
    [string]$Port = "3306",
    [string]$User = "root",
    [string]$Password = "123456"
)

$ErrorActionPreference = "Stop"
$sqlDir = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) "sql"

Get-ChildItem -Path $sqlDir -Filter "*.sql" | Sort-Object Name | ForEach-Object {
    Write-Host "executing $($_.Name)"
    & $MysqlBin -h $Host -P $Port -u $User "-p$Password" --default-character-set=utf8mb4 -e "source $($_.FullName)"
}

Write-Host "MySQL schemas initialized."
