# dev-env-arrange
简化安装各种开发工具的烦恼,提供一键安装和环境切换的功能

## xml 操作符号支持

1. `>`   读取xml  输入path     返回 doc
2. `<`   保存xml  输入doc path,返回 doc 
3. `+=`  修改xml  输入doc xpath value 返回doc

> `xml D:/settings.xml < /d:settings/d:localRepository  D:/rep += D:/settings.xml <`
>> 整个表达式使用的是逆波兰表达式 
>> `xml`代表选择的符号体系 
>> `D:/settings.xml` 要操作的文件 
>> `>`  `D:/settings.xml` 转换为一个可以操作 `Document`
>> `/d:settings/d:localRepository` 操作节点的xpath
>> d 代表默认的命名空间
>> `D:/rep` 需要修改的值
>> `+=` 修改值 `localRepository` 不存在是将被创建并赋值
>> `D:/settings.xml` 修改后doc需要保存的位置
>> `<` 保存到指定位置


# 未完成