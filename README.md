dummy-server
============

A very simple http server used to run a httpserver for prototyping and development purpose, to use simple download the jar [here](https://github.com/erickzanardo/dummy-server/raw/master/dist/dummy-server.jar) and run:

```shell
java -jar dummy-server.jar path/to/your/resource/folder
```

You can create mocking services too, for the times you need some client/server interaction on your prototype, your mocking services are created using JavaScript and should be in a separated folder, then run:

```shell
java -jar dummy-server.jar path/to/your/resource/folder path/to/your/mocking/services
```

An very simple example of a service:
```javascript
var service = {
    get: function(req, resp) {
        resp.print("Hello World!");
    }
};
```

For a complete reference for the request and response functions check this [link](https://github.com/erickzanardo/rambi#request-and-response)
