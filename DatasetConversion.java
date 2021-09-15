package dataframe;

import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static util.Constants.*;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

//
// Explore interoperability between DataFrame and Dataset. Note that Dataset
// is covered in much greater detail in the 'dataset' directory.
//
public class DatasetConversion {

    //
    // This must be a JavaBean in order for Spark to infer a schema for it
    //
    public static class Cust implements Serializable {
        private String id;
        private String ts;
        private String tsType;

        public Cust(String id, String ts, String tsType) {
            this.id = id;
            this.ts = ts;
            this.tsType = tsType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }
        public String getTsType() {
            return tsType;
        }

        public void setTsType(String tsType) {
            this.tsType = tsType;
        }
    }
        
    
    public static void registerColumnUppercaseUdf(SQLContext sqlContext) {

        sqlContext.udf().register(COLUMN_UPPERCASE_UDF_NAME, (UDF1<String, String>)
            (columnValue) -> {

                return columnValue.toUpperCase();

            }, DataTypes.StringType);
    }
    
    public static void registerConvertToUTCUdf(SQLContext sqlContext) {

        sqlContext.udf().register("convertToUTCUdf", (UDF1<String, String>)
            (columnValue) -> {
            	System.out.println("columnValue:: "+columnValue );
            	String utcTime = OffsetDateTime.parse(columnValue).withOffsetSameInstant(ZoneOffset.UTC).toString();
        		return utcTime;

            }, DataTypes.StringType);
    }
    
    public static void registerConvertToESTUdf(SQLContext sqlContext) {

        sqlContext.udf().register("convertToESTUdf", (UDF1<String, String>)
            (columnValue) -> {
            	System.out.println("columnValue1:: "+columnValue );
            	String estTime=OffsetDateTime.parse(columnValue).atZoneSameInstant(ZoneId.of("America/New_York")).toString();
        		return estTime.replace("[America/New_York]", "");

            }, DataTypes.StringType);
    }
    
    public static void main(String[] args) {
        SparkSession spark = SparkSession
            .builder()
            .appName("DataFrame-DatasetConversion")
            .master("local[4]")
            .getOrCreate();

        registerColumnUppercaseUdf(spark.sqlContext());
        registerConvertToUTCUdf(spark.sqlContext());
        registerConvertToESTUdf(spark.sqlContext());
        
       // spark.sqlContext();
        //
        // The Java API requires you to explicitly instantiate an encoder for
        // any JavaBean you want to use for schema inference
        //
        Encoder<Cust> custEncoder = Encoders.bean(Cust.class);
        //
        // Create a container of the JavaBean instances
        //
        List<Cust> estData = Arrays.asList(
                new Cust("1","2017-08-01T14:30:45.111-04:00","est"),
                new Cust("2","2021-03-12T02:30:45.111-05:00","est"));
        
        List<Cust> utcData = Arrays.asList(
                new Cust("1","1997-10-27T14:20:45Z","utc"),
                new Cust("2","2004-04-15T16:10:22Z","utc"));
        //
        // Use the encoder and the container of JavaBean instances to create a
        // Dataset
        //
        Dataset<Cust> estDS = spark.createDataset(estData, custEncoder);
        Dataset<Cust> utcDS = spark.createDataset(utcData, custEncoder);

        System.out.println("*** here is the schema inferred from the Cust bean");
        estDS.printSchema();

        System.out.println("*** here is the data");
        estDS.show(false);
        
        
        //
        // Querying a Dataset of any type results in a
        // DataFrame (i.e. Dastaset<Row>)
        //

//        Dataset<Row> smallerDF =
//                ds.select("sales", "state").filter(col("state").equalTo("CA"));
//
//        System.out.println("*** here is the dataframe schema");
//        smallerDF.printSchema();
//
//        System.out.println("*** here is the data");
//        smallerDF.show();
        
       // Dataset<Row> upperCaseColumnDataset = ds.withColumn(UPPSERCASE_NAME_COLUMN_NAME,
       //         callUDF(COLUMN_UPPERCASE_UDF_NAME, col("tsType")));

       // upperCaseColumnDataset.show();
        
//        Dataset<Row> utcDs = ds.withColumn("ts_utc",
//                callUDF("convertToUTCUdf", col("ts")));
//
//        utcDs.show(false);
//        
//        Dataset<Row> estDs = utcDs.withColumn("ts_est",
//                callUDF("convertToESTUdf", col("ts")));
//
//        estDs.show(false);
        
        estDS.createOrReplaceTempView("oldEST");
        utcDS.toDF().createOrReplaceTempView("oldUTC");
        
        Dataset<Row> ds1 =  spark.sql("select *,case when tsType='utc' then ts else convertToUTCUdf(ts) end as ts_utc ,case when tsType='utc' then convertToESTUdf(ts) else ts end as ts_est from oldEST");
        ds1.show(false);
      
        Dataset<Row> ds2 =  spark.sql("select *,case when tsType='utc' then ts else convertToUTCUdf(ts) end as ts_utc ,case when tsType='utc' then convertToESTUdf(ts) else ts end as ts_est from oldUTC");
        ds2.show(false);
        
        spark.stop();
    }
}
