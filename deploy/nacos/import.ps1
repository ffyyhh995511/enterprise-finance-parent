param(
    [string]$NacosAddr = "http://127.0.0.1:8848",
    [string]$Username = "nacos",
    [string]$Password = "nacos"
)

$ErrorActionPreference = "Stop"
$configDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$login = Invoke-RestMethod -Method Post -Uri "$NacosAddr/nacos/v1/auth/login?username=$Username&password=$Password"
$token = $login.accessToken
if (-not $token) {
    throw "Failed to login Nacos, check address and credentials."
}

function Publish-NacosConfig {
    param(
        [string]$DataId,
        [string]$Group,
        [string]$Type,
        [string]$FilePath
    )
    $content = Get-Content -Raw -Encoding UTF8 -Path $FilePath
    $body = @{
        dataId      = $DataId
        group       = $Group
        type        = $Type
        content     = $content
        accessToken = $token
    }
    $result = Invoke-RestMethod -Method Post -Uri "$NacosAddr/nacos/v1/cs/configs" -Body $body
    Write-Host "published $Group / $DataId => $result"
}

Publish-NacosConfig -DataId "enterprise-finance-common.yml" -Group "DEFAULT_GROUP" -Type "yaml" -FilePath "$configDir\enterprise-finance-common.yml"
Publish-NacosConfig -DataId "enterprise-finance-user.yml" -Group "DEFAULT_GROUP" -Type "yaml" -FilePath "$configDir\enterprise-finance-user.yml"
Publish-NacosConfig -DataId "enterprise-finance-order.yml" -Group "DEFAULT_GROUP" -Type "yaml" -FilePath "$configDir\enterprise-finance-order.yml"
Publish-NacosConfig -DataId "enterprise-finance-product.yml" -Group "DEFAULT_GROUP" -Type "yaml" -FilePath "$configDir\enterprise-finance-product.yml"
Publish-NacosConfig -DataId "enterprise-finance-gateway.yml" -Group "DEFAULT_GROUP" -Type "yaml" -FilePath "$configDir\enterprise-finance-gateway.yml"
Publish-NacosConfig -DataId "enterprise-finance-user-sentinel-degrade" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-user-sentinel-degrade.json"
Publish-NacosConfig -DataId "enterprise-finance-user-sentinel-flow" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-user-sentinel-flow.json"
Publish-NacosConfig -DataId "enterprise-finance-order-sentinel-degrade" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-order-sentinel-degrade.json"
Publish-NacosConfig -DataId "enterprise-finance-order-sentinel-flow" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-order-sentinel-flow.json"
Publish-NacosConfig -DataId "enterprise-finance-product-sentinel-degrade" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-product-sentinel-degrade.json"
Publish-NacosConfig -DataId "enterprise-finance-product-sentinel-flow" -Group "SENTINEL_GROUP" -Type "json" -FilePath "$configDir\enterprise-finance-product-sentinel-flow.json"

Write-Host "Nacos configs imported."
