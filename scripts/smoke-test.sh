#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost}"

services=(
    "user-service:8081"
    "task-service:8082"
    "payment-service:8083"
)

echo " CampusRunner 微服务冒烟测试"

for service in "${services[@]}"; do
    name="${service%%:*}"
    port="${service##*:}"
    url="${BASE_URL}:${port}/actuator/health"

    echo
    echo "检查 ${name} ..."

    response=$(curl -fsS --max-time 5 "$url") || {
        echo "[失败] ${name} 无法访问"
        exit 1
    }

    if echo "$response" | grep -q '"status":"UP"'; then
        echo "[通过] ${name} 状态正常"
    else
        echo "[失败] ${name} 健康状态异常"
        echo "$response"
        exit 1
    fi
done

echo
echo " 所有微服务健康检查通过！"