## http4s CRUD for Devices DB with Cats, Doobie, Circe

Cats v2.3, Circe v0.8, Doobie v0.10, Http4s v0.21

### Native image

See https://github.com/TheDIM47/http4s-crud/blob/main/native-image-readme.md

### Configuration

See application.conf or use command line -Dconfig.file=path/to/config-file
See also: https://github.com/lightbend/config

```
db.driver="org.h2.Driver"
db.url="jdbc:h2:mem:test"
db.user="sa"
db.pass=""

server.ip="127.0.0.1
server.port=8888
```

### Logs

See logback.xml, http://logback.qos.ch/manual/configuration.html

### Database table

```sql
CREATE TABLE IF NOT EXISTS DEVICE (
  ID VARCHAR(36) NOT NULL,
  CREATED_DATE DATETIME(3),
  VERSION INT(11),
  CAM_ID VARCHAR(255),
  DESCRIPTION VARCHAR(255),
  IMEI VARCHAR(255) NOT NULL,
  NAME VARCHAR(255),
  SERIAL_VERSION VARCHAR(255),
  TELEPHONE VARCHAR(255),
  IS_HIDDEN NUMBER(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY (ID)
);
```

### Device class

```scala
case class Device(
    id: Option[String],
    created: Timestamp,
    version: Long,
    camId: String,
    description: Option[String],
    imei: String,
    name: String,
    serial: Option[String],
    phone: Option[String],
    isHidden: Boolean
)
```

### Query samples

#### Find all devices
```
curl --location --request GET 'localhost:8888/devices'
```

#### Find device by id

```
curl --location --request GET 'localhost:8888/device/816ab85e-47f9-43b5-b662-95600e540800'
```

return found device or 404

```json
{
    "id": "816ab85e-47f9-43b5-b662-95600e540800",
    "created": 1579666224000,
    "version": 1,
    "camId": "1742708",
    "description": "some description",
    "imei": "700020003000000",
    "name": "name",
    "serial": "serial",
    "phone": "+7(444)444-4444",
    "isHidden": false
}
```

#### Find device by IMEI

```
curl --location --request GET 'localhost:8888/device/imei/752625173859491'
```

#### Find device by "Camera ID"

```
curl --location --request GET 'localhost:8888/device/camid/1742708'
```

#### Add device to DB

device id will be assigned automatically
return new device id (UUID staring)

```
curl --location --request POST 'localhost:8888/device' \
--header 'Content-Type: application/json' \
--data-raw '{
    "created": 1582469154000,
    "version": 1,
    "camId": "2742718",
    "description": "some description",
    "imei": "720500047000000",
    "name": "name",
    "serial": "serial",
    "phone": "+7(000)000-0000",
    "isHidden": false
}'
```

#### Update device information

```
curl --location --request PUT 'localhost:8888/device' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "816ab85e-47f9-43b5-b662-95600e540800",
    "created": 1579000999000,
    "version": 100,
    "camId": "1700000",
    "imei": "702123074560700",
    "name": "some new name",
    "phone": "+7(111)111-1111",
    "isHidden": true
}'
```

#### Delete device

```
curl --location --request DELETE 'localhost:8888/device/816ab85e-47f9-43b5-b662-95600e540800'
```

