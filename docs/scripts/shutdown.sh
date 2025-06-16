#!/bin/bash

# 애플리케이션 종료 스크립트
# 실행 중인 Java 프로세스를 찾아 종료합니다.

# 현재 디렉토리 기준으로 실행
cd "$(dirname "$0")"

# 실행 중인 Java 프로세스 찾기
PID=$(ps -ef | grep java | grep seesaw-web | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
  echo "실행 중인 애플리케이션이 없습니다."
  exit 0
fi

echo "애플리케이션 프로세스($PID) 종료 중..."
kill -15 $PID

# 프로세스가 종료될 때까지 대기
WAIT_COUNT=0
while [ $WAIT_COUNT -lt 10 ]; do
  if ! ps -p $PID > /dev/null; then
    echo "애플리케이션이 정상적으로 종료되었습니다."
    exit 0
  fi
  
  echo "애플리케이션 종료 대기 중... ($WAIT_COUNT/10)"
  WAIT_COUNT=$((WAIT_COUNT+1))
  sleep 1
done

# 10초 후에도 종료되지 않으면 강제 종료
if ps -p $PID > /dev/null; then
  echo "애플리케이션이 응답하지 않습니다. 강제 종료합니다."
  kill -9 $PID
  sleep 1
fi

echo "애플리케이션 종료 완료"
exit 0