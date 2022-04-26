package com.ttdeye.stock;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.ttdeye.stock.common.base.controller.BaseController;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 快速生成
 * </p>
 *
 * @author lanjerry
 * @since 2021-09-16
 */
public class FastAutoGeneratorTest {

    /**
     * 执行初始化数据库脚本
     */
//    public static void before() throws SQLException {
//        Connection conn = DATA_SOURCE_CONFIG.build().getConn();
////        InputStream inputStream = H2CodeGeneratorTest.class.getResourceAsStream("/sql/init.sql");
//        ScriptRunner scriptRunner = new ScriptRunner(conn);
//        scriptRunner.setAutoCommit(true);
////        scriptRunner.runScript(new InputStreamReader(inputStream));
//        conn.close();
//    }

    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig
        .Builder("jdbc:mysql://127.0.0.1:3306/ttdeye-stock", "psi", "123456");

    /**
     * 执行 run
     */
    public static void main(String[] args) throws SQLException {
//        before();
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
            // 全局配置
            .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称")).dateType(DateType.ONLY_DATE))
            // 包配置
            .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名")))
            // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .controllerBuilder()
                        .superClass(BaseController.class)
                        .enableRestStyle()
                        .enableHyphenStyle()
                        .entityBuilder()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE)
                        )


                        .build())






            /*
                模板引擎配置，默认 Velocity 可选模板引擎 zymBeetl 或 Freemarker
               .templateEngine(new BeetlTemplateEngine())
               .templateEngine(new FreemarkerTemplateEngine())
             */
            .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
