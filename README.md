<img src="http://www.takes.org/logo.png" width="96px" height="96px"/>

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/C3FFK3YAY.svg)](https://www.0crat.com/contrib/C3FFK3YAY)

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3FFK3YAY.svg)](https://www.0crat.com/p/C3FFK3YAY)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/takes)](http://www.rultor.com/p/yegor256/takes)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://img.shields.io/travis/yegor256/takes/master.svg)](https://travis-ci.org/yegor256/takes)
[![Build status](https://img.shields.io/appveyor/ci/yegor256/takes/master.svg)](https://ci.appveyor.com/project/yegor256/takes/branch/master)
[![Javadoc](http://www.javadoc.io/badge/org.takes/takes.svg)](http://www.javadoc.io/doc/org.takes/takes)
[![codebeat badge](https://codebeat.co/badges/5721bba4-59cd-44ea-a60f-6043734187f7)](https://codebeat.co/projects/github-com-yegor256-takes)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/938e7683fcfa4db9bff362c2b4a9b78e)](https://www.codacy.com/app/github_90/takes?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=yegor256/takes&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/takes/blob/master/LICENSE.txt)

[![jpeek report](http://i.jpeek.org/org.takes/takes/badge.svg)](http://i.jpeek.org/org.takes/takes/)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/takes.svg)](https://codecov.io/github/yegor256/takes?branch=master)
[![SonarQube](https://img.shields.io/badge/sonar-ok-green.svg)](https://sonarcloud.io/dashboard?id=org.takes%3Atakes)

[![Maven Central](https://img.shields.io/maven-central/v/org.takes/takes.svg)](https://maven-badges.herokuapp.com/maven-central/org.takes/takes)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/takes)](http://www.0pdd.com/p?name=yegor256/takes)

**Takes** is a [true object-oriented](http://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html)
and [immutable](http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html)
Java8 web development framework. Its key benefits, comparing to all others, include these
four fundamental principles:

 1. not a single `null` ([why NULL is bad?](http://www.yegor256.com/2014/05/13/why-null-is-bad.html))
 2. not a single `public` `static` method ([why they are bad?](http://www.yegor256.com/2014/05/05/oop-alternative-to-utility-classes.html))
 3. not a single mutable class ([why they are bad?](http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html))
 4. not a single `instanceof` keyword, type casting, or reflection ([why?](http://www.yegor256.com/2015/04/02/class-casting-is-anti-pattern.html))

Of course, there are no configuration files.
Besides that, these are more traditional features, out of the box:

 * hit-refresh debugging
 * [XML+XSLT](http://www.yegor256.com/2014/06/25/xml-and-xslt-in-browser.html)
 * [JSON](http://en.wikipedia.org/wiki/JSON)
 * [RESTful](http://en.wikipedia.org/wiki/Representational_state_transfer)
 * Templates, incl. [Apache Velocity](http://velocity.apache.org/)

This is what is not supported and won't be supported:

 * [WebSockets](http://en.wikipedia.org/wiki/WebSocket)

This [blog post](http://www.yegor256.com/2015/03/22/takes-java-web-framework.html) may help you too.

These two web systems use Takes, and they are open source:
[wring.io](http://www.wring.io) ([sources](https://github.com/yegor256/wring)),
[jare.io](http://www.jare.io) ([sources](https://github.com/yegor256/jare)).

## Contents

- [Quick Start](#quick-start)
- [Build and Run With Maven](#build-and-run-with-maven)
- [Build and Run With Gradle](#build-and-run-with-gradle)
- [Unit Testing](#unit-testing)
- [Integration Testing](#integration-testing)
- [A Bigger Example](#a-bigger-example)
    - [Front Interface](#front-interface)
    - [Back Interface](#back-interface)
- [Templates](#templates)
	- [Velocity Templates](#velocity-templates)
- [Static Resources](#static-resources)
- [Hit Refresh Debugging](#hit-refresh-debugging)
- [Request Methods (POST, PUT, HEAD, etc.)](#request-methods-post-put-head-etc)
- [Request Parsing](#request-parsing)
- [Form Processing](#form-processing)
- [Exception Handling](#exception-handling)
- [Redirects](#redirects)
- [RsJSON](#rsjson)
- [RsXembly](#rsxembly)
- [GZIP Compression](#gzip-compression)
- [SSL Configuration](#ssl-configuration)
- [Authentication](#authentication)
- [Command Line Arguments](#command-line-arguments)
- [Logging](#logging)
- [Directory Layout](#directory-layout)
- [Optional dependencies](#optional-dependencies)
- [Backward compatibility](#backward-compatibility)
- [How to contribute](#how-to-contribute)
- [Got questions?](#got-questions)

## Quick Start

Create this `App.java` file:

```java
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
public final class App {
  public static void main(final String... args) throws Exception {
    new FtBasic(
      new TkFork(new FkRegex("/", "hello, world!")), 8080
    ).start(Exit.NEVER);
  }
}
```

Then, download [`takes.jar`](http://repo1.maven.org/maven2/org/takes/takes/) and compile your Java code:

```
$ javac -cp takes.jar App.java
```

Now, run it like this:

```bash
$ java -Dfile.encoding=UTF-8 -cp takes.jar:. App
```

Should work :)

This code starts a new HTTP server on port 8080 and renders a plain-text page on
all requests at the root URI.

**Important**: Pay attention that UTF-8 encoding is set on the command line.
The entire framework relies on your default Java encoding, which is not
necessarily UTF-8 by default. To be sure, always set it on the command line
with `file.encoding` Java argument. We decided not to hard-code "UTF-8" in
our code mostly because this would be against the entire idea of Java localization,
according to which a user always should have a choice of encoding and language
selection. We're using `Charset.defaultCharset()` everywhere in the code.

## Build and Run With Maven

If you're using Maven, this is how your `pom.xml` should look like:

```xml
<project>
  <dependencies>
    <dependency>
      <groupId>org.takes</groupId>
      <artifactId>takes</artifactId>
    </dependency>
  </dependencies>
  <profiles>
    <profile>
      <id>hit-refresh</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.3</version>
            <executions>
              <execution>
                <id>start-server</id>
                <phase>pre-integration-test</phase>
                <goals>
                  <goal>java</goal>
                </goals>
              </execution>
            </executions>
            <configuration>
              <mainClass>foo.App</mainClass> <!-- your main class -->
              <cleanupDaemonThreads>false</cleanupDaemonThreads>
              <arguments>
                <argument>--port=${port}</argument>
              </arguments>
            </configuration>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
</project>
```

With this configuration you can run it from command line:

```
$ mvn clean integration-test -Phit-refresh -Dport=8080
```

Maven will start the server and you can see it at `http://localhost:8080`.

## Using in servlet app

Create a take with constructor accepting `ServletContext`:

```java
package com.myapp;

public final class TkApp implements Take {
  private final ServletContext ctx;

  public TkApp(final ServletContext context) {
    this.ctx = context;
  }

  @Override
  public Response act(final Request req) throws IOException {
    return new RsText("Hello servlet!");
  }
}
```

Add `org.takes.servlet.SrvTake` to your `web.xml`, don't forget to specify
take class as servlet `init-param`:

```xml
  <servlet>
    <servlet-name>takes</servlet-name>
    <servlet-class>org.takes.servlet.SrvTake</servlet-class>
    <init-param>
      <param-name>take</param-name>
      <param-value>com.myapp.TkApp</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>takes</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>
```

## Build and Run With Gradle

If you're using Gradle, this is how your `build.gradle` should look like:

```gradle
plugins {
    id 'java'
    id 'application'
}

repositories {
    mavenCentral()
}

dependencies {
    compile group: 'org.takes', name: 'takes', version: '1.11.3'
}

mainClassName='foo.App' //your main class
```

With this configuration you can run it from command line:

```gradle
$ gradle run -Phit-refresh -Dport=8080
```
Gradle will start the server and you can see it at `http://localhost:8080`.

## Unit Testing

This is how you can unit test the app, using JUnit 4.x and
[Hamcrest](http://hamcrest.org):

```java
public final class AppTest {
  @Test
  public void returnsHttpResponse() throws Exception {
    MatcherAssert.assertThat(
      new RsPrint(
        new App().act(new RqFake("GET", "/"))
      ).printBody(),
      Matchers.equalsTo("hello, world!")
    );
  }
}
```


You can create a fake request with form parameters like this:

```java
new RqForm.Fake(
  new RqFake(),
  "foo", "value-1",
  "bar", "value-2"
)
```

## Integration Testing

Here is how you can test the entire server via HTTP, using JUnit and
[jcabi-http](http://http.jcabi.com) for making HTTP requests:

```java
public final class AppITCase {
  @Test
  public void returnsTextPageOnHttpRequest() throws Exception {
    new FtRemote(new App()).exec(
      new FtRemote.Script() {
        @Override
        public void exec(final URI home) throws IOException {
          new JdkRequest(home)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(Matchers.equalTo("hello, world!"));
        }
      }
    );
  }
}
```

More complex integration testing examples you can find in one
of the open source projects that are using Take, for example:
[rultor.com](https://github.com/yegor256/rultor/tree/master/src/test/java/com/rultor/web).

## A Bigger Example

Let's make it a bit more sophisticated:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFork(
        new FkRegex("/robots\\.txt", ""),
        new FkRegex("/", new TkIndex())
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

The `FtBasic` is accepting new incoming sockets on port 8080,
parses them according to HTTP 1.1 specification and creates instances
of class `Request`. Then, it gives requests to the instance of `TkFork`
(`tk` stands for "take") and expects it to return an instance of `Take` back.
As you probably understood already, the first regular expression that matches
returns a take. `TkIndex` is our custom class,
let's see how it looks:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsHtml("<html>Hello, world!</html>");
  }
}
```

It is immutable and must implement a single method `act()`, which is returning
an instance of `Response`. So far so good, but this class doesn't have an access
to an HTTP request. Here is how we solve this:

```java
new TkFork(
  new FkRegex(
    "/file/(?<path>[^/]+)",
    new TkRegex() {
      @Override
      public Response act(final RqRegex request) throws IOException {
        final File file = new File(
          request.matcher().group("path")
        );
        return new RsHTML(
          FileUtils.readFileToString(file, Charsets.UTF_8)
        );
      }
    }
  )
)
```

We're using `TkRegex` instead of `Take`, in order to deal with
`RqRegex` instead of a more generic `Request`. `RqRegex` gives an instance
of `Matcher` used by `FkRegex` for pattern matching.

Here is a more complex and verbose example:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFork(
        new FkRegex("/robots.txt", ""),
        new FkRegex("/", new TkIndex()),
        new FkRegex(
          "/xsl/.*",
          new TkWithType(new TkClasspath(), "text/xsl")
        ),
        new FkRegex("/account", new TkAccount(users)),
        new FkRegex("/balance/(?<user>[a-z]+)", new TkBalance())
      )
    ).start(Exit.NEVER);
  }
}
```

## Front interface

Essential part of [Bigger Example](#a-bigger-example) is
[Front](src/main/java/org/takes/http/Front.java) interface.
It's encapsulates server's [back-end](src/main/java/org/takes/http/Back.java)
and used to start an instance, which will accept requests and return results.
`FtBasic`, which is a basic front, implements that interface - you've
seen it's usage in above mentioned example.

There are other useful implementations of this interface:

* The [FtRemote](src/main/java/org/takes/http/FtRemote.java)
class allows you to provide script, that will be executed against
given front. You can see how it's used in [integration tests](#integration-testing).
* The [FtCli](src/main/java/org/takes/http/FtCli.java) class
allows you to start your application with command line arguments. More details
in [Command Line Arguments](#command-line-arguments).
* The [FtSecure](src/main/java/org/takes/http/FtSecure.java) class allows
you to start your application with SSL. More details in
[SSL Configuration](#ssl-configuration).

## Back interface

[Back](src/main/java/org/takes/http/Back.java) interface is the back-end that
is responsible for IO operations on TCP network level. There are various useful
implementations of that interface:

* The [BkBasic](src/main/java/org/takes/http/BkBasic.java) class is a basic
implementation of the `Back` interface. It is responsible for accepting the
request from `Socket`, converting the socket's input to the
[Request](src/main/java/org/takes/Request.java), dispatching it to the
provided [Take](src/main/java/org/takes/Take.java) instance, getting
the result and printing it to the socket's output until all the request is
fulfilled.
* The [BkParallel](src/main/java/org/takes/http/BkParallel.java) class is
a decorator of the `Back` interface, that is responsible for running the
back-end in parallel threads. You can specify the number of threads or try
to use the default number, which depends on available processors number in JVM.
* The [BkSafe](src/main/java/org/takes/http/BkSafe.java) class is a decorator
of the `Back` interface, that is responsible for running the back-end in a
safe mode. That means that it will ignore exception thrown from original `Back`.
* The [BkTimeable](src/main/java/org/takes/http/BkTimeable.java) class is a
decorator of the `Back` interface, that is responsible for running the back-end
for specified maximum lifetime in milliseconds. It is constantly checking if
the thread with original `back` exceeds provided limit and if so - it's
interrupts the thread of that `back`.
* The [BkWrap](src/main/java/org/takes/http/BkWrap.java) class is a convenient
wrap over the original `Back` instance. It's just delegates the `accept`
to that `Back` and might be useful if you want to add your own decorators of the
`Back` interface. This class is used in `BkParallel` and `BkSafe` as a parent class.

## Templates

Now let's see how we can render something more complex than an plain text.
First, XML+XSLT is a recommended mechanism of HTML rendering. Even though it may be
too complex, give it a try, you won't regret. Here is how we render a simple XML
page that is transformed to HTML5 on-fly (more about `RsXembly` read below):

```java
public final class TkAccount implements Take {
  private final Users users;
  public TkAccount(final Users users) {
    this.users = users;
  }
  @Override
  public Response act(final Request req) {
    final User user = this.users.find(new RqCookies(req).get("user"));
    return new RsLogin(
      new RsXSLT(
        new RsXembly(
          new XeStylesheet("/xsl/account.xsl"),
          new XeAppend("page", user)
        )
      ),
      user
    );
  }
}
```

This is how that `User` class may look like:

```java
public final class User implements XeSource {
  private final String name;
  private final int balance;
  @Override
  public Iterable<Directive> toXembly() {
    return new Directives().add("user")
      .add("name").set(this.name).up()
      .add("balance").set(Integer.toString(this.balance));
  }
}
```

Here is how `RsLogin` may look like:

```java
public final class RsLogin extends RsWrap {
  public RsLogin(final Response response, final User user) {
    super(
      new RsWithCookie(
        response, "user", user.toString()
      )
    );
  }
}
```

## Velocity Templates

Let's say, you want to use [Velocity](http://velocity.apache.org/):

```java
public final class TkHelloWorld implements Take {
  @Override
  public Response act(final Request req) {
    return new RsVelocity(
      "hi, ${user.name}! You've got ${user.balance}",
      new RsVelocity.Pair("user", new User())
    );
  }
}
```

You will need this extra dependency in classpath:

```xml
<dependency>
  <groupId>org.apache.velocity</groupId>
  <artifactId>velocity-engine-core</artifactId>
  <scope>runtime</scope>
</dependency>
```

For Gradle users:

```gradle
dependencies {
    ...
    runtime group: 'org.apache.velocity', name: 'velocity-engine-core', version: 'x.xx'//put the version here
    ...
}
```

## Static Resources

Very often you need to serve static resources to your web users, like CSS
stylesheets, images, JavaScript files, etc. There are a few supplementary
classes for that:

```java
new TkFork(
  new FkRegex("/css/.+", new TkWithType(new TkClasspath(), "text/css")),
  new FkRegex("/data/.+", new TkFiles(new File("/usr/local/data"))
)
```

Class `TkClasspath` take static part of the request URI and finds a resource with this name in classpath.

`TkFiles` just looks by file name in the directory configured.

`TkWithType` sets content type of all responses coming out of the decorated take.

## Hit Refresh Debugging

It is a very convenient feature. Once you start the app you want to be able to
modify its static resources (CSS, JS, XSL, etc), refresh the page in a browser
and immediately see the result. You don't want to re-compile the entire project
and restart it. Here is what you need to do to your sources in order to enable
that feature:

```java
new TkFork(
  new FkRegex(
    "/css/.+",
    new TkWithType(
      new TkFork(
        new FkHitRefresh(
          "./src/main/resources/foo/scss/**", // what sources to watch
          "mvn sass:compile", // what to run when sources are modified
          new TkFiles("./target/css")
        )
        new FkFixed(new TkClasspath())
      ),
      "text/css"
    )
  )
)
```

This `FkHitRefresh` fork is a decorator of take. Once it sees
`X-Take-Refresh` header in the request, it realizes that the server is running in
"hit-refresh" mode and passes the request to the encapsulated take. Before it
passes the request it tries to understand whether any of the resources
are older than compiled files. If they are older, it tries
to run compilation tool to build them again.

## Request Methods (POST, PUT, HEAD, etc.)

Here is an example:

```java
new TkFork(
  new FkRegex(
    "/user",
    new TkFork(
      new FkMethods("GET", new TkGetUser()),
      new FkMethods("POST,PUT", new TkPostUser()),
      new FkMethods("DELETE", new TkDeleteUser())
    )
  )
)
```

## Request Parsing

Here is how you can parse an instance of `Request`:

```java
Href href = new RqHref.Base(request).href();
URI uri = href.uri();
Iterable<String> values = href.param("key");
```

For a more complex parsing try to use Apache Http Client or something
similar.

## Form Processing

Here is an example:

```java
public final class TkSavePhoto implements Take {
  @Override
  public Response act(final Request req) {
    final String name = new RqForm(req).param("name");
    return new RsWithStatus(HttpURLConnection.HTTP_NO_CONTENT);
  }
}
```

## Exception Handling

By default, `TkFork` lets all exceptions bubble up. If one of your take
crashes, a user will see a default error page. Here is how you can configure
this behavior:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFallback(
        new TkFork(
          new FkRegex("/robots\\.txt", ""),
          new FkRegex("/", new TkIndex())
        ),
        new FbChain(
          new FbStatus(404, new RsText("sorry, page is absent")),
          new FbStatus(405, new RsText("this method is not allowed here")),
          new Fallback() {
            @Override
            public Iterator<Response> route(final RqFallback req) {
              return Collections.<Response>singleton(
                new RsHTML("oops, something went terribly wrong!")
              ).iterator();
            }
          }
        )
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

`TkFallback` decorates an instance of Take and catches all exceptions any of
its take may throw. Once it's thrown, an instance of `FbChain` will
find the most suitable fallback and will fetch a response from there.

## Redirects

Sometimes it's very useful to return a redirect response (`30x` status code),
either by a normal `return` or by throwing an exception. This example
illustrates both methods:

```java
public final class TkPostMessage implements Take {
  @Override
  public Response act(final Request req) {
    final String body = new RqPrint(req).printBody();
    if (body.isEmpty()) {
      throw new RsForward(
        new RsFlash("message can't be empty")
      );
    }
    // save the message to the database
    return new RsForward(
      new RsFlash(
        "thanks, the message was posted"
      ),
      "/"
    );
  }
}
```

Then, you should decorate the entire `TkFork` with this `TkForward` and `TkFlash`:

```java
public final class App {
  public static void main(final String... args) {
    new FtBasic(
      new TkFlash(
        new TkForward(
          new TkFork(new FkRegex("/", new TkPostMessage())
        )
      ),
      8080
    ).start(Exit.NEVER);
  }
}
```

## RsJSON

Here is how we can deal with JSON:

```java
public final class TkBalance extends TkFixed {
  @Override
  public Response act(final RqRegex request) {
    return new RsJSON(
      new User(request.matcher().group("user")))
    );
  }
}
```

This is the method to add to `User`:

```java
public final class User implements XeSource, RsJSON.Source {
  @Override
  public JsonObject toJSON() {
    return Json.createObjectBuilder()
      .add("balance", this.balance)
      .build();
  }
}
```

## RsXembly

Here is how you generate an XML page using [Xembly](http://www.xembly.org):

```java
Response response = new RsXembly(
  new XeAppend("page"),
  new XeDirectives("XPATH '/page'", this.user)
)
```

This is a complete example, with all possible options:

```java
Response response = new RsXembly(
  new XeStylesheet("/xsl/account.xsl"), // add processing instruction
  new XeAppend(
    "page", // create a DOM document with "page" root element
    new XeMillis(false), // add "millis" attribute to the root, with current time
    user, // add user to the root element
    new XeSource() {
      @Override
      public Iterable<Directive> toXembly() {
        return new Directives().add("status").set("alive");
      }
    },
    new XeMillis(true), // replace "millis" attribute with take building time
  ),
)
```

This is the output that will be produced:

```xml
<?xml version='1.0'?>
<?xsl-stylesheet href='/xsl/account.xsl'?>
<page>
  <millis>5648</millis>
  <user>
    <name>Jeff Lebowski</name>
    <balance>123</balance>
  </user>
  <status>alive</status>
</page>
```

To avoid duplication of all this scaffolding in every page, you can create your
own class, which will be used in every page, for example:

```java
Response response = new RsXembly(
  new XeFoo(user)
)
```

This is how this `XeFoo` class would look like:

```java
public final class XeFoo extends XeWrap {
  public XeFoo(final String stylesheet, final XeSource... sources) {
    super(
      new XeAppend(
        "page",
        new XeMillis(false),
        new XeStylesheet(stylesheet),
        new XeChain(sources),
        new XeSource() {
          @Override
          public Iterable<Directive> toXembly() {
            return new Directives().add("status").set("alive");
          }
        },
        new XeMillis(true)
      )
    );
  }
}
```

You will need this extra dependency in classpath:

```xml
<dependency>
  <groupId>com.jcabi.incubator</groupId>
  <artifactId>xembly</artifactId>
</dependency>
```

More about this mechanism in this blog post:
[XML Data and XSL Views in Takes Framework](http://www.yegor256.com/2015/06/25/xml-data-xsl-views-takes-framework.html).

## Cookies

Here is how we drop a cookie to the user:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsWithCookie("auth", "John Doe");
  }
}
```

An HTTP response will contain this header, which will place
a `auth` cookie into the user's browser:

```
HTTP/1.1 200 OK
Set-Cookie: auth="John Doe"
```

This is how you read cookies from a request:

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    // the list may be empty
    final Iterable<String> cookies = new RqCookies(req).cookie("my-cookie");
  }
}
```

## GZIP Compression

If you want to compress all your responses with GZIP, wrap your take in
`TkGzip`:

```java
new TkGzip(take)
```

Now, each request that contains `Accept-Encoding` request header with `gzip`
compression method inside will receive a GZIP-compressed response. Also,
you can compress an individual response, using `RsGzip` decorator.

## Content Negotiation

Say, you want to return different content based on `Accept` header
of the request (a.k.a. [content negotation](http://en.wikipedia.org/wiki/Content_negotiation)):

```java
public final class TkIndex implements Take {
  @Override
  public Response act(final Request req) {
    return new RsFork(
      req,
      new FkTypes("text/*", new RsText("it's a text"))
      new FkTypes("application/json", new RsJSON("{\"a\":1}"))
      new FkTypes("image/png", /* something else */)
    );
  }
}
```
## SSL Configuration

First of all, setup your keystore settings, for example

```java
final String file = this.getClass().getResource("/org/takes/http/keystore").getFile();
final String password = "abc123";
System.setProperty("javax.net.ssl.keyStore", file);
System.setProperty("javax.net.ssl.keyStorePassword", password);
System.setProperty("javax.net.ssl.trustStore", file);
System.setProperty("javax.net.ssl.trustStorePassword", password);
```

Then simple create exemplar of class [FtSecure](src/main/java/org/takes/http/FtSecure.java) with socket factory

```java
final ServerSocket skt = SSLServerSocketFactory.getDefault().createServerSocket(0);
new FtRemote(
  new FtSecure(new BkBasic(new TkFixed("hello, world")), skt),
  skt,
  true
);
```

## Authentication

Here is an example of login via [Facebook](https://developers.facebook.com/docs/reference/dialogs/oauth/):

```java
new TkAuth(
  new TkFork(
    new FkRegex("/", new TkHTML("hello, check <a href='/acc'>account</a>")),
    new FkRegex("/acc", new TkSecure(new TkAccount()))
  ),
  new PsChain(
    new PsCookie(
      new CcSafe(new CcHex(new CcXOR(new CcCompact(), "secret-code")))
    ),
    new PsByFlag(
      new PsByFlag.Pair(
        PsFacebook.class.getSimpleName(),
        new PsFacebook("facebook-app-id", "facebook-secret")
      ),
      new PsByFlag.Pair(
        PsLogout.class.getSimpleName(),
        new PsLogout()
      )
    )
  )
)
```

Then, you need to show a login link to the user, which he or she
can click and get to the Facebook OAuth authentication page. Here is how
you do this with XeResponse:

```java
new RsXembly(
  new XeStylesheet("/xsl/index.xsl"),
  new XeAppend(
    "page",
    new XeFacebookLink(req, "facebook-app-id"),
    // ... other xembly sources
  )
)
```

The link will be add to the XML page like this:

```xml
<page>
  <links>
    <link rel="take:facebook" href="https://www.facebook.com/dialog/oauth..."/>
  </links>
</page>
```

Similar mechanism can be used for `PsGithub`, `PsGoogle`, `PsLinkedin`, `PsTwitter`, etc.

This is how you get currently logged in user:

```java
public final class TkAccount implements Take {
  @Override
  public Response act(final Request req) {
    final Identity identity = new RqAuth(req).identity();
    if (this.identity.equals(Identity.ANONYMOUS)) {
      // returns "urn:facebook:1234567" for a user logged in via Facebook
      this.identity().urn();
    }
  }
}
```

More about it in this blog post:
[How Cookie-Based Authentication Works in the Takes Framework](http://www.yegor256.com/2015/05/18/cookie-based-authentication.html)

## Command Line Arguments

There is a convenient class `FtCLI` that parses command line arguments and
starts the necessary `Front` accordingly.

There are a few command line arguments that should be passed to
`FtCLI` constructor:

```
--port=1234         Tells the server to listen to TCP port 1234
--lifetime=5000     The server will die in five seconds (useful for integration testing)
--hit-refresh       Run the server in hit-refresh mode
--daemon            Runs the server in Java daemon thread (for integration testing)
--threads=30        Processes incoming HTTP requests in 30 parallel threads
--max-latency=5000  Maximum latency in milliseconds per each request
                    (longer requests will be interrupted)
```

For example:

```java
public final class App {
  public static void main(final String... args) {
    new FtCLI(
      new TkFork(new FkRegex("/", "hello, world!")),
      args
    ).start(Exit.NEVER);
  }
}
```

Then, run it like this:

```
$ java -cp take.jar App.class --port=8080 --hit-refresh
```

You should see "hello, world!" at `http://localhost:8080`.

Parameter `--port` also accepts file name, instead of a number. If the file
exists, `FtCLI` will try to read its content and use it as
port number. If the file is absent, `FtCLI` will allocate a new random
port number, use it to start a server, and save it to the file.

## Logging

The framework sends all logs to SLF4J logging facility. If you want to see them,
configure one of [SLF4J bindings](http://www.slf4j.org/manual.html).

To make a `Take` log, wrap it in a `TkSlf4j` - for example:

     new TkSlf4j(
         new TkFork(...)
     )

## Directory Layout

You are free to use any build tool, but we recommend Maven. This is how your project directory layout may/should look like:

```
/src
  /main
    /java
      /foo
        App.java
    /scss
    /coffeescript
    /resources
      /vtl
      /xsl
      /js
      /css
      robot.txt
      log4j.properties
  /test
    /java
      /foo
        AppTest.java
    /resources
      log4j.properties
pom.xml
LICENSE.txt
```

## Optional dependencies

If you're using Maven and include Takes as a dependency in your own project,
you can choose which of the optional dependencies to include in your project.
The list of all of the optional dependencies can be seen in the Takes project `pom.xml`.

For example, to use the Facebook API shown above, simply add a dependency to
the `restfb` API in your project:

```
<dependency>
  <groupId>com.restfb</groupId>
  <artifactId>restfb</artifactId>
  <version>1.15.0</version>
  <scope>runtime</scope>
</dependency>
```

For Gradle, you should add the dependencies as usual:

```gradle
dependencies {
    ...
    runtime group: 'com.restfb', name: 'restfb', version: '1.15.0'
}
```

## Backward compatibility
Version 2.0 is not backward compatible with previous versions.

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use maven 3.2+.

Pay attention that our `pom.xml` inherits a lot of configuration
from [jcabi-parent](http://parent.jcabi.com).
[This article](http://www.yegor256.com/2015/02/05/jcabi-parent-maven-pom.html)
explains why it's done this way.

For testing using Gradle and you must first migrate the Maven project to `Gradle` using the following command to generate the `build.gradle` file based on the information in the pom.xml:

```
$ gradle init
```

And also to avoid frustrations, before requesting the pull, please execute the complete build:

```
$ gradle build -x test
```

## Got questions?

If you have questions or general suggestions, don't hesitate to submit
a new [Github issue](https://github.com/yegor256/takes/issues/new).
