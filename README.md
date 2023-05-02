# dev-env-arrange
简化安装各种开发工具的烦恼,提供一键安装和环境切换的功能

## 自定义工具安装配置文件

- 示例 

```json
{
    "name":"maven3",
    "variable":{
        "config":"${root}/conf/settings.xml"
    },
    "check":"./mvn -v",
    "download":{
        "urlRoot":[
            "https://dlcdn.apache.org/maven/maven-3"
        ],
        "process":"common",
        "url":"${urlRoot}/${version}/binaries/apache-maven-${version}-bin.${osFormat}",
        "osFormat": {
            "os-win-format": "zip",
            "os-linux-format": "tar.gz"
        }
    },
    "operate":[
        "xml ${config} > /d:settings/d:localRepository  \"test\" += ${config} <",
        "sysEnv:MAVEN_HOME_SHY ${root} = path ${PATH} ;%MAVEN_HOME_SHY%/bin +"
        ]
}
```
- name
  - 工具名称 
- variable
  - 处理`operate`属性中使用到的填充变量
- check
  - 安装完成后检查是否安装成功
- download 
  - 下载处理相关
- download.urlRoot
  - 下载相关工具的根路径 一般是版本上一个目录
  - 取决于`download.process`属性
  - 支持变量填充 拥有特殊变量`urlRoot`
- download.process
  - 需要使用的下载处理器
  - 主要是下载文件和处理对应的版本信息
- download.url
  - 下载文件的格式
  - `version` 是输入的版本
  - `osFormat` 会根据系统自动选择,和`download.osFormat`相关
- operate
  - 下载工具后需要配置的东西
  - 支持变量填充
  - xml文件修改等
  - 文件移动复制创建等
  - 环境变量修改
  - 支持更多操作
  - 支持变量填充 拥有特殊变量`root`

- 特殊变量
  - `root` 工具下载解压后的根目录
  - `version` 输入的版本号
  - `urlRoot` 下载的版本上一级目录
  - `osFormat` 会根据系统选择相应的变量

## xml 操作符号支持

1. `>`   读取xml  输入path     返回 doc
2. `<`   保存xml  输入doc path,返回 doc 
3. `+=`  修改xml  输入doc xpath value 返回doc

### xml 操作列子
> `xml D:/settings.xml < /d:settings/d:localRepository  D:/rep += D:/settings.xml <`
>> 整个表达式使用的是逆波兰表达式 
> 
>> `xml`代表选择的符号体系 
> 
>> `D:/settings.xml` 要操作的文件
> 
>> `>` 可以将 `D:/settings.xml` 转换为一个可以操作 `Document`
> 
>> `/d:settings/d:localRepository` 操作节点的xpath
> 
>> d 代表默认的命名空间
> 
>> `D:/rep` 需要修改的值
> 
>> `+=` 修改值 `localRepository` 不存在是将被创建并赋值
> 
>> `D:/settings.xml` 修改后doc需要保存的位置
> 
>> `<` 保存到指定位置


# 未完成