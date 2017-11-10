# BusinessParameters
Provides an API and middle-ware to enable management of application parameters by business users.

## In progress
This documentation and other support is currently very limited, but will be improved as we go.

## Getting started

To get started with your custom business parameters, you need to do three things:
1. [Deploy the platform](#deploying-the-platform)
1. [Write your custom group definitions](#writing-group-definitions)
1. [Use it in your application](#writing-a-client-application)

Or, if you want to do some quick testing, you can replace the custom steps with some [quick tests](#doing-some-quick-tests).

Keep in mind that the current technology support is limited.
We suggest to deploy on Karaf and use the direct Java client API.

### Deploying the platform
#### On Karaf
##### Features
The platform provides Karaf _features_ to ease deployment.
There are specific features for each technology choice (although these are rather limited at the moment).
Dependencies external to the platform are defined in separate features. These can be used for convenience.
You can also ignore them and manage dependencies in a way suitable to your environment. 

Currently, the only supported client-service protocol is direct Java, 
meaning that both service and client must be deployed on the same server.

* Service-side
  * Select a backend storage
    * ElasticSearch
    * In-memory (for testing)
  * Select adapters (to expose the service)
    * Direct Java
    * JMX
* Client-side
  * Select the client corresponding to the selected adapter
    * Direct Java
* Install your group _definitions_
  * Both service-side and client-side

##### Feature overview
Type | Module | Main feature  | External dependency feature | Configuration
--- | --- | --- | --- | ---
| **Backends**
| | Elastic Search | businessparameters-elasticsearch | businessparameters-basic-elasticsearch-deps | be.kwakeroni.parameters.backend.elasticsearch.cfg
| | In-memory | businessparameters-inmemory | _N/A_ | _N/A_
| **Adapters**
| | Direct Java | businessparameters-adapter-direct | _N/A_ | _N/A_
| | JMX | businessparameters-adapter-jmx | businessparameters-basic-jmx-deps | _N/A_
| **Clients**
| | Direct Java | _businessparameters-adapter-direct_ | _N/A_ | _N/A_
    
##### Example
```shell
> repo-add mvn:be.kwakeroni.parameters/parameters/0.1.0-SNAPSHOT/xml/features
> feature:install businessparameters-basic-elasticsearch-deps  // external dependencies 
> feature:install businessparameters-elasticsearch 
> feature:install businessparameters-adapter-direct 
> feature:install businessparameters-adapter-jmx 
```


### Consulting JMX Console
The installed groups can be consulted in JMX as described below.
Pay attention that not all data from the backing storage will be displayed,
only deployed group definitions will be taken into account.

1. Start jconsole
2. Connect to the process of the backend server (e.g. org.apache.karaf.main.Main)
3. Lookup be.kwakeroni.parameters in the MBeans tab
4. Below the backend node, you'll find all defined groups
5. Find more information about the available operations in each node (for example: legal values for the 'parameter' parameter)

### Writing Group Definitions
#### Dependencies
To write definitions you will need the `parameters-definition-api` module.
You will also need the `parameters-basic-common` module to facilitate the creation of basic definitions.
Optionally, you can include the `parameters-types-support` to easily assign common types to your parameters.

```xml
<project>
    ...
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>be.kwakeroni.parameters.definition</groupId>
                <artifactId>parameters-definition</artifactId>
                <version>${project.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>be.kwakeroni.parameters.basic</groupId>
                <artifactId>parameters-basic</artifactId>
                <version>0.1.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>be.kwakeroni.parameters.types</groupId>
                <artifactId>parameters-types</artifactId>
                <version>${project.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>            
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>be.kwakeroni.parameters.definition</groupId>
            <artifactId>parameters-definition-api</artifactId>
        </dependency>
        <dependency>
            <groupId>be.kwakeroni.parameters.basic</groupId>
            <artifactId>parameters-basic-common</artifactId>
        </dependency>
        <dependency>
            <groupId>be.kwakeroni.parameters.types</groupId>
            <artifactId>parameters-types-support</artifactId>
        </dependency>        
    </dependencies>
    ...
</project>
```
#### Writing a definition
A fluent definition API is available for your convenience.
Notice the usage of static imports, coming from the `be.kwakeroni.parameters.basic.definition.BasicGroup` class.
The following example will give you a mapped-ranged group named _my.group_, with parameters _myKey_, _myIndex_ and _myValue_.
```java
    public static final ParameterGroupDefinition MY_DEFINITION =
            mappedGroup()
                .withKeyParameter("myKey")
                .mappingTo(rangedGroup()
                    .withRangeParameter("myIndex", ParameterTypes.INT)
                    .mappingTo(group()
                        .withParameter("myValue")
                    ))
            .build("my.group");
```
#### Deploying a definition
##### Stand-alone
By simply deploying the definition as an osgi service, it will automatically be picked up by the backend service.

##### As part of a catalog
Multiple definitions can be grouped in an instance of `ParameterGroupDefinitionCatalog`.
Osgi services of that interface will also be picked up by the backend.
```java
public class MyCatalog implements ParameterGroupDefinitionCatalog {
    @Override
    public Stream<ParameterGroupDefinition> stream() {
        return Arrays.asList(
                MyGroup.DEFINITION,
                MyOtherGroup.DEFINITION
                ).stream();
    }
}
```
```xml
    <service interface="be.kwakeroni.parameters.definition.api.catalog.ParameterGroupDefinitionCatalog">
        <bean class="my.pkg.MyCatalog"/>
    </service>
```


### Writing a client application
#### Dependencies
In the client application you will need the `parameters-client-api` module to access the BusinessParameters service facade.
You will also need the `parameters-basic-client` module to be able to create basic queries.
```xml
<project>
    ...
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>be.kwakeroni.parameters.client</groupId>
                <artifactId>parameters-client</artifactId>
                <version>0.1.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>be.kwakeroni.parameters.basic</groupId>
                <artifactId>parameters-basic</artifactId>
                <version>0.1.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>be.kwakeroni.parameters.client</groupId>
            <artifactId>parameters-client-api</artifactId>
        </dependency>
        <dependency>
            <groupId>be.kwakeroni.parameters.basic</groupId>
            <artifactId>parameters-basic-client</artifactId>
        </dependency>
    </dependencies>
    ...
</project>
```
#### Using Business Parameters and Queries
The fluent client API is not yet available.
Business Parameters are called by constructing a Query object:
```java
BusinessParameters parameters = ...
String value = parameters.get(MyOtherGroup.instance(),
    new ValueQuery<>(MyOtherGroup.SIMPLE_PARAM)).get();
```
Or using nested queries for more complex groups:
```java
String myValue = parameters.get(MyGroup.instance(),
        new MappedQuery<>(key, ParameterTypes.STRING,
                new RangedQuery<>(index, ParameterTypes.INT,
                        new ValueQuery<>(MyGroup.MY_VALUE)))).get();
```                                
### Doing some quick tests
While we don't have a demo _petshop_ application yet, 
the rudimentary _scratch_ module can currently be used for executing some basic tests.
1. Run a test Elastic Search server, which automatically sets up some data
```shell
scratch$ ./runESTestServer.sh
```
2. Deploy the test definitions
```shell
karaf> install -s mvn:be.kwakeroni.parameters/scratch/0.1.0-SNAPSHOT
```