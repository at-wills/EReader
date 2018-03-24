## 项目结构及分工

```
├── app
├── ereader-base
│   ├── contract				———— vp层协议接口
│   ├── db						———— database 相关（xxDAO 由 greendao 生成）
│   ├── entity					———— orm 实体模型，即 javabean
│   ├── event					———— eventbus 相关
│   ├── repository				———— M层，数据来源可能为sp、db、api
│   ├── subscriber				———— rxandroid 相关
│   ├── ui						———— ui 相关（activity只作为一层壳，实际UI放到fragment中）
│   └── utils					———— 工具类
├── ereader-home（书架页）
├── ereader-impt（导入页）
└── ereader-read（阅读页）
    └── presenter				———— P层实现类
```

* app 为应用的入口模块，不做修改！！！
* ereader-base 为公用基础模块，包含了大量的 base 抽象类及一些通用工具类，不由个人进行修改！！！
* ereader-home、ereader-impt、ereader-read 为三个功能模块，模块的负责人只修改该模块代码，自己负责模块功能的正确性。功能模块只能使用 ereader-base 模块中的内容，而不能使用其他功能模块中的内容

**注**：每个人只修改自己负责那个模块的目录，如果需要修改到 app、ereader-base 中的内容（如添加权限、修改工具类），把问题放 issue 中（最好能提供解决方案），然后统一处理

## 修改启动 activity

为了能够方便地调试自己对应的模块功能，需在本地修改 app 模块中 AndroidManifest.xml 文件配置

```xml
<activity android:name=".read.ui.activity.ReadActivity"> // 修改为自己所需的启动activity
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**注意**：提交版本库时，不要将 app 模块的修改提交上来

## 增加 db 测试数据

由于模块 ereader-home、ereader-read 缺少添加数据库信息的方式，不利于进行测试。可在 app 模块中 ReadApplication 类中 testDb 方法，人为插入所需数据

```java
public class ReaderApplication extends BaseApplication {

    @Override
    public void onCreate() {...}

    private void testDb() {
        BookDao bookDao =  DbHelper.getInstance().getSession().getBookDao();
        Book book = new Book();
        book.setTitle("test1");
        bookDao.insert(book);
    }
}
```

该函数只在 app 第一次运行的时候会执行，故为了使该方法修改生效，需要先把 app 卸载掉（或者清空数缓存数据），才能在重新运行时生效

**注意**：提交版本库时，不要将 app 模块的修改提交上来
