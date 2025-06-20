### This is the Java version of Prebid Server. See the Prebid Server [Feature List](https://docs.prebid.org/prebid-server/features/pbs-feature-idx.html) and [FAQ entry](https://docs.prebid.org/faq/prebid-server-faq.html#why-are-there-two-versions-of-prebid-server-are-they-kept-in-sync) to understand the differences between PBS-Java and [PBS-Go](https://github.com/prebid/prebid-server).

# Prebid Server (Java)

[![GitHub version](https://badge.fury.io/gh/prebid%2fprebid-server-java.svg)](http://badge.fury.io/gh/prebid%2fprebid-server-java)
[![GitHub contributors](https://img.shields.io/github/contributors/prebid/prebid-server-java.svg)](https://GitHub.com/prebid/prebid-server-java/contributors/)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/prebid/prebid-server-java/blob/master/docs/contributing.md) 
[![GitHub pull-requests closed](https://img.shields.io/github/issues-pr-closed/prebid/prebid-server-java.svg)](https://GitHub.com/prebid/prebid-server-java/pull/)

Prebid Server is an open source implementation of Server-Side Header Bidding.
It is managed by Prebid.org,
and upholds the principles from the [Prebid Code of Conduct](https://prebid.org/wrapper_code_of_conduct.html).

This project does not support the same set of Bidders as Prebid.js, although there is overlap.
The current set can be found in the [adapters](./src/main/java/org/prebid/server/bidder) package. If you don't see the one you want, feel free to [contribute it](docs/developers/add-new-bidder.md).

For more information, see:

- [What is Prebid?](https://prebid.org/why-prebid/)
- [Getting started with Prebid Server](https://docs.prebid.org/prebid-server/overview/prebid-server-overview.html)
- [Current Bidders](https://docs.prebid.org/dev-docs/pbs-bidders.html)

Please consider [registering your Prebid Server](https://docs.prebid.org/prebid-server/hosting/pbs-hosting.html#optional-registration) to get on the mailing list for updates, etc.

# Getting Started

The server makes the following assumptions:
- No ranking or decisioning is performed by this server. It just proxies requests.
- No ad quality management (e.g., malware, viruses, deceptive creatives) is performed by this server.
- This server does no fraud scanning and does nothing to prevent bad traffic.
- This server logs errors but not requests.
- This server has no user profiling or user data collection capabilities.

This project is built upon [Vert.x](http://vertx.io) to achieve high request throughput. 
We use [Maven](https://maven.apache.org) and attempt to introduce minimal dependencies.

When running, the server responds to several HTTP [endpoints](docs/endpoints).

## Building

Follow next steps to create JAR file which can be deployed locally.

- Install prerequsites
  - Java SDK: Oracle's or Corretto. Let us know if there's a distribution PBS-Java doesn't work with.
  - Java SDK Version: 21
  - Maven
  
- Clone the project:
```bash
git clone https://github.com/prebid/prebid-server-java.git
```

- Move to project directory:
```bash
cd prebid-server-java
```

And from this step there are two common use cases, which can be chosen depending on your goals 

1. Create prebid-server JAR only

- First, make sure nothing's running on your local port 8080 or 8090
- Run below command to build project:
```bash
mvn clean package
```

2. Create prebid-server JAR with modules
- Run below command to build project:
```bash
mvn clean package --file extra/pom.xml
```
For more information how to build the server follow [documentation](docs/build.md).

## Configuration

The source code includes an example configuration file `sample/configs/prebid-config.yaml`.
Also, check the account settings file `sample/configs/sample-app-settings.yaml`.

For more information how to configure the server follow [documentation](docs/config.md). There are many settings you'll want to consider such as which bidders you're going to enable, privacy defaults, admin endpoints, etc.


## Running

Run your local server with the command:
```bash
java -jar target/prebid-server.jar --spring.config.additional-location=sample/configs/prebid-config.yaml
```
For more options how to start the server, please follow [documentation](docs/run.md).

## Running prebuilt .jar
Starting from PBS Java v2.9, you can download prebuilt .jar packages from [Release Notes](https://github.com/prebid/prebid-server-java/releases) page, instead of building them by yourself. 
This prebuilt packages are delivered with or without extra modules.

## Verifying

To check the server is started go to [http://localhost:8080/status](http://localhost:8080/status) 
and verify response status is `200 OK`.

There are a couple of 'hello world' test requests described in sample/requests/README.txt

## Running Docker image

Starting from PBS Java v3.11.0, you can download prebuilt Docker images from [GitHub Packages](https://github.com/orgs/prebid/packages?repo_name=prebid-server-java) page,
and use them instead of plain .jar files. These prebuilt images are delivered in 2 flavors:
- https://github.com/prebid/prebid-server-java/pkgs/container/prebid-server-java is a bare PBS and doesn't contain modules.
- https://github.com/prebid/prebid-server-java/pkgs/container/prebid-server-java-bundle is a "bundle" that contains PBS and all the modules.

To run PBS from image correctly, you should provide the PBS config file. The easiest way is to mount the config file into the container,
using [--mount or --volume (-v) Docker CLI arguments](https://docs.docker.com/engine/reference/commandline/run/).
Keep in mind that the config file should be mounted into a specific location: ```/app/prebid-server/conf/``` or ```/app/prebid-server/```.

PBS follows the regular Spring Boot config load hierarchy and type.
For simple configuration, a single `application.yaml` mounted to `/app/prebid-server/conf/` will be enough.
Please consult [Spring Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html) for all possible ways to configure PBS.

You can also supply command-line parameters through `JAVA_OPTS` environment variable which will be appended to the `java` command before the `-jar ...` parameter.
Please pay attention to line breaks and escape them if needed.

Example execution using sample configuration:
```shell
docker run --rm -v ./sample:/app/prebid-server/sample:ro -p 8060:8060 -p 8080:8080 ghcr.io/prebid/prebid-server-java:latest --spring.config.additional-location=sample/configs/prebid-config.yaml
```
or
```shell
docker run --rm -v ./sample:/app/prebid-server/sample:ro -p 8060:8060 -p 8080:8080 -e JAVA_OPTS=-Dspring.config.additional-location=sample/configs/prebid-config.yaml ghcr.io/prebid/prebid-server-java:latest
```

# Documentation

## Development
- [Endpoints](https://docs.prebid.org/prebid-server/endpoints/pbs-endpoint-overview.html)
- [Adding new bidder](https://docs.prebid.org/prebid-server/developers/add-new-bidder-java.html)
- [Adding new analytics module](https://docs.prebid.org/prebid-server/developers/pbs-build-an-analytics-adapter.html#adding-an-analytics-adapter-in-pbs-java)
- [Adding viewability support](docs/developers/add-viewability-vendors.md)
- [Auction result post-processing](docs/auction-result-post-processing.md)
- [Cookie Syncs](https://docs.prebid.org/prebid-server/developers/pbs-cookie-sync.html)
- [Stored Requests](docs/developers/stored-requests.md)
- [Unit Tests](docs/developers/unit-tests.md)
- [Functional Tests](docs/developers/functional-tests.md)
- [GDPR](docs/gdpr.md)

## Maintenance
- [Build for local](docs/build.md)
- [Build for AWS](docs/build-aws.md)
- [Configure application](docs/config.md)
  - [Full list of configuration options](docs/config-app.md)
  - [Application settings](docs/application-settings.md)
- [Run with optimizations](docs/run.md)
- [Metrics](docs/metrics.md)

## Contributing
- [Contributing](docs/developers/contributing.md)
- [Code Style](docs/developers/code-style.md)
- [Code Review](docs/developers/code-reviews.md)
- [Versioning](docs/developers/versioning.md)
