#!/bin/bash

# 애플리케이션 시작 스크립트
# 사용법: ./startup.sh <JAR 파일명>

# 현재 디렉토리 기준으로 실행
cd "$(dirname "$0")"

# 인자 확인
if [ $# -lt 1 ]; then
  echo "오류: JAR 파일명을 지정해야 합니다."
  echo "사용법: ./startup.sh <JAR 파일명>"
  exit 1
fi

JAR_FILE="$1"

# JAR 파일 존재 확인
if [ ! -f "$JAR_FILE" ]; then
  echo "오류: JAR 파일($JAR_FILE)을 찾을 수 없습니다."
  exit 1
fi

# 이미 실행 중인 프로세스 확인
PID=$(ps -ef | grep java | grep "$JAR_FILE" | grep -v grep | awk '{print $2}')
if [ ! -z "$PID" ]; then
  echo "경고: 애플리케이션이 이미 실행 중입니다(PID: $PID)."
  echo "계속 진행하려면 먼저 shutdown.sh를 실행하세요."
  exit 1
fi

# 애플리케이션 시작
echo "애플리케이션 시작 중: $JAR_FILE"
# 로그 디렉토리 생성
mkdir -p logs
nohup java -jar "$JAR_FILE" --server.address=0.0.0.0 --spring.profiles.active=prod > /dev/null 2>&1 &

# 시작 확인
sleep 2
NEW_PID=$(ps -ef | grep java | grep "$JAR_FILE" | grep -v grep | awk '{print $2}')
if [ -z "$NEW_PID" ]; then
  echo "오류: 애플리케이션 시작에 실패했습니다."
  exit 1
fi

echo "애플리케이션이 성공적으로 시작되었습니다(PID: $NEW_PID)."
echo "로그는 logs/application.log 파일에서 확인할 수 있습니다."
exit 0
