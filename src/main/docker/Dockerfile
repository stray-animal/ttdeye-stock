############################################################
# Dockerfile to build ttdeye-stock images
# Based on jdk8
############################################################
#
FROM 18600285979/ttdeye-openjdk8
MAINTAINER [name:yongming.zhang,email:702346318@qq.com]
### 应用名称
ENV APP_NAME ttdeye-stock
### 应用版本
ENV APP_VER  0.0.1
ENV APP_ARCH  x86_64
ENV APP_JarNAME APP.jar
VOLUME /tmp
ADD maven/ttdeye-stock.jar ${APP_JarNAME}
#ADD commit_id.txt /commit_id.txt
ENV gclogfile="/data/logs/${APP_NAME}/${APP_NAME}.gc.log"
ENV JAVA_OPTS="-XX:+UseG1GC \
-Xmx384m \
-XX:MaxGCPauseMillis=200 \
-Xloggc:${gclogfile}  \
-XX:+PrintGCApplicationStoppedTime \
-XX:+PrintGCApplicationConcurrentTime"

VOLUME ["/data/logs/${APP_NAME}"]
CMD java ${JAVA_OPTS}  -jar /${APP_JarNAME}