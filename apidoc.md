# apidoc生成格式说明
```js
define({ "api": [
  {
    "type": "get",
    "url": "/returnTest",
    "title": "",
    "group": "test",
    "description": "<p>这是一个无参数方法，但是有返回一个基础类型</p>",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "bool",
            "optional": false,
            "field": "header1",
            "description": "<p>头1</p>"
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "string",
            "size": "..5",
            "allowedValues": [
              "\"1\"",
              "\"2\""
            ],
            "optional": false,
            "field": "param1",
            "description": "<p>哈哈</p>"
          },
          {
            "group": "Parameter",
            "type": "number",
            "size": "1-80",
            "allowedValues": [
              "1",
              "2",
              "3"
            ],
            "optional": false,
            "field": "param2",
            "description": "<p>哈哈2</p>"
          }
        ]
      }
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "type": "test",
            "optional": false,
            "field": "hahah",
            "description": "<p>这是错误的使用</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Return",
          "content": "这里用来写return注释",
          "type": "json"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "int",
            "optional": false,
            "field": "test",
            "description": "<p>基础测试</p>"
          }
        ]
      }
    },
    "version": "1.1.1",
    "filename": "./src/main/java/com/test/TestController.java",
    "groupTitle": "test",
    "name": "GetReturntest"
  }
] });
```

