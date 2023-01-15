package top.flagshen.myqq.common;

/**
 * mybatis-plus逆向工程：代码生成工具
 */
public class MybatisPlusGenerator {

    /**
     * Scanner string.
     *
     * @param tip the tip
     * @return the string
     *//*
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    *//**
     * The entry point of application.
     *
     * @param args the input arguments
     *//*
    public static void main(String[] args) {
        *//**
         * 代码生成器
         *//*
        AutoGenerator mpg = new AutoGenerator();

        String parentPackageName = "top.flagshen.myqq";

        *//**
         * 全局配置
         *//*
        GlobalConfig globalConfig = new GlobalConfig();
        //生成文件的输出目录
        final String projectPath = System.getProperty("user.dir");
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        //Author设置作者
        globalConfig.setAuthor(System.getProperty("user.name"));
        //是否覆盖文件
        globalConfig.setFileOverride(true);
        //生成后打开文件
        globalConfig.setOpen(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setDateType(DateType.ONLY_DATE);
        globalConfig.setEntityName("%sDO");
        globalConfig.setXmlName("%sMapper");
        globalConfig.setServiceName("I%sService");
        globalConfig.setControllerName("%sController");
        globalConfig.setMapperName("%sMapper");
        globalConfig.setServiceImplName("%sServiceImpl");

        mpg.setGlobalConfig(globalConfig);

        *//**
         * 数据源配置
         *//*
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        // 数据库类型,默认MYSQL
        dataSourceConfig.setDbType(DbType.MYSQL);
        //自定义数据类型转换
        dataSourceConfig.setTypeConvert(new MySqlTypeConvert());
        dataSourceConfig
                .setUrl("jdbc:mysql://localhost/seven?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false");
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("root");

        mpg.setDataSource(dataSourceConfig);


        *//**
         * 包配置
         *//*
        final PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        //父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
        pc.setParent(parentPackageName);
        mpg.setPackageInfo(pc);


        *//**
         * 自定义配置
         *//*
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        *//**
         * 模板
         *//*
        //如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";


        *//**
         * 自定义输出配置
         *//*
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mybatis/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        *//**
         * 配置模板
         *//*
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        *//**
         * 策略配置
         *//*
        StrategyConfig strategy = new StrategyConfig();
        //设置命名格式
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setInclude(scanner("表名,多个英文逗号分割").split(","));
        //实体是否为lombok模型（默认 false）
        strategy.setEntityLombokModel(true);
        //生成 @RestController 控制器
        strategy.setRestControllerStyle(true);
        //设置自定义基础的Entity类，公共字段
        strategy.setSuperEntityColumns("id");
        //驼峰转连字符
        strategy.setControllerMappingHyphenStyle(true);
        //表名前缀
        strategy.setTablePrefix("test_", "teemp_", "tsys_", "teomp_");
        strategy.setEntitySerialVersionUID(true);
        strategy.setLogicDeleteFieldName("is_delete");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }*/
}
