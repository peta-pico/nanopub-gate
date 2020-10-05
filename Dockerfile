# Pull base image
FROM tomcat:8-jre8

# Remove default webapps:
RUN rm -fr /usr/local/tomcat/webapps/*

COPY target/nanopub-gate /usr/local/tomcat/nanopub-gate/target/nanopub-gate
RUN ln -s /usr/local/tomcat/nanopub-gate/target/nanopub-gate /usr/local/tomcat/webapps/ROOT

# Port:
EXPOSE 8080

CMD ["catalina.sh", "run"]
