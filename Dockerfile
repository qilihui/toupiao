FROM registry.cn-hangzhou.aliyuncs.com/acs/maven:3-jdk-8

ENV MY_HOME=/work
RUN mkdir -p $MY_HOME
WORKDIR $MY_HOME
ADD pom.xml $MY_HOME

# get all the downloads out of the way
RUN ["/usr/local/bin/mvn-entrypoint.sh","mvn","verify","clean","--fail-never","-Dmaven.test.skip=true"]

# add source
ADD . $MY_HOME

# run maven verify
RUN ["/usr/local/bin/mvn-entrypoint.sh","mvn","package","-Dmaven.test.skip=true"]
ENTRYPOINT ["java","-Dspring.profiles.active=aliyun","-jar","/work/target/demo-0.0.1-SNAPSHOT.jar"]