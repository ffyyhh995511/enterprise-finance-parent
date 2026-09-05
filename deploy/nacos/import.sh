#!/usr/bin/env bash
set -euo pipefail

NACOS_ADDR="${NACOS_ADDR:-http://127.0.0.1:8848}"
USERNAME="${NACOS_USERNAME:-nacos}"
PASSWORD="${NACOS_PASSWORD:-nacos}"
CONFIG_DIR="$(cd "$(dirname "$0")" && pwd)"

TOKEN="$(curl -s -X POST "${NACOS_ADDR}/nacos/v1/auth/login?username=${USERNAME}&password=${PASSWORD}" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')"
if [[ -z "${TOKEN}" ]]; then
  echo "Failed to login Nacos"
  exit 1
fi

publish() {
  local data_id="$1"
  local group="$2"
  local type="$3"
  local file="$4"
  curl -s -X POST "${NACOS_ADDR}/nacos/v1/cs/configs" \
    -d "dataId=${data_id}" \
    -d "group=${group}" \
    -d "type=${type}" \
    --data-urlencode "content@${file}" \
    -d "accessToken=${TOKEN}"
  echo " published ${group} / ${data_id}"
}

publish "enterprise-finance-common.yml" "DEFAULT_GROUP" "yaml" "${CONFIG_DIR}/enterprise-finance-common.yml"
publish "enterprise-finance-user.yml" "DEFAULT_GROUP" "yaml" "${CONFIG_DIR}/enterprise-finance-user.yml"
publish "enterprise-finance-order.yml" "DEFAULT_GROUP" "yaml" "${CONFIG_DIR}/enterprise-finance-order.yml"
publish "enterprise-finance-gateway.yml" "DEFAULT_GROUP" "yaml" "${CONFIG_DIR}/enterprise-finance-gateway.yml"
publish "enterprise-finance-user-sentinel-degrade" "SENTINEL_GROUP" "json" "${CONFIG_DIR}/enterprise-finance-user-sentinel-degrade.json"
publish "enterprise-finance-user-sentinel-flow" "SENTINEL_GROUP" "json" "${CONFIG_DIR}/enterprise-finance-user-sentinel-flow.json"
publish "enterprise-finance-order-sentinel-degrade" "SENTINEL_GROUP" "json" "${CONFIG_DIR}/enterprise-finance-order-sentinel-degrade.json"
publish "enterprise-finance-order-sentinel-flow" "SENTINEL_GROUP" "json" "${CONFIG_DIR}/enterprise-finance-order-sentinel-flow.json"

echo "Nacos configs imported."
