#include <WiFiManager.h>
#include <NTPClient.h>
#include <SPIFFS.h>
#include <DHT.h>
#include <MQ7.h>
#include <ArduinoJson.h>
#include <BluetoothSerial.h>

#define DHT_PIN 4
#define MQ7_PIN 2
#define LED_PIN 12
#define DHT_TYPE DHT11
#define BUTTON_PIN 23
#define BUTTON_TIME_TL 100

const size_t json_capacity = JSON_OBJECT_SIZE(5)+20;      // 80 + 20 -> 100
StaticJsonDocument<json_capacity> measurement;            // Objeto json de medição atual dos sensores
String measurementSerialized;                             // String do json para envio na API
DynamicJsonDocument json_config(JSON_OBJECT_SIZE(2));     // Objeto json de configuração

char id[7]="";                                            // 6 caracteres do id do equipamento + 1 pra \0
char BASE_URL[48]="";                                     // 47 caracteres pro link + 1 para \0
bool should_save_config = false;                          // Variável para saber se houve alterações no Wifi Manager

DHT dht_sensor(DHT_PIN,DHT_TYPE);                         // Objeto para leitura dos dados do DHT
MQ7 mq7(MQ7_PIN, 3.3);                                    // Objeto para leitura de CO do MQ-7

WiFiUDP ntpUDP;                                           // Objeto de comunicação UDP para troca de dados no servidor NTP
NTPClient timeClient(ntpUDP,"pool.ntp.org",-14400,6000);  // (objeto UDP, nome do servidor NTP, -4h sobre o GMT, intervalo de atualização)

BluetoothSerial serial_bt;                                // Interface serial para comunicação por blueetooth

WiFiClient wifiClient;                                    // Cliente Wifi para envio de dados para API por HTTP

char PATH_SAVE_MEASUREMENT[18]="/auth/measurement";       // Rota no Buri API para envio de dados

volatile boolean mode_is_online = true;                   // Variável que controle o modo de operação do dispositivo online(true) offline(false)

float dht_values = 0.0;                                   // Variável que recebe os dados do sendor DHT

unsigned long button_last_press = 0;                      // Marca o último instante que o botão foi pressionado (debouncing)

void saveConfigCallback(){
  Serial.println("LOG:Should save config");
  should_save_config = true;
}

void IRAM_ATTR buttonPressedISR(){
  unsigned long current_time = millis();
  if((current_time - button_last_press) > BUTTON_TIME_TL){
    mode_is_online = !mode_is_online;
    button_last_press = current_time;
  }
}

void setup() {
  Serial.begin(9600);
  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(MQ7_PIN,INPUT);
  pinMode(LED_PIN,OUTPUT);
  attachInterrupt(digitalPinToInterrupt(BUTTON_PIN), buttonPressedISR, RISING);
  WiFiManager wm;
  bool res;
  if (!serial_bt.begin("Buri-Hardware")){
    Serial.println("LOG:Erro ao iniciar o Bluetooth.");
    return;
  }
  Serial.println("LOG:Bluetooth iniciado.");
  WiFiManagerParameter cst_eqpId("eqpId","Id do equipamento",id,6);
  WiFiManagerParameter cst_apiUrl("urlAPI","URL base da API",BASE_URL,47);
  wm.setSaveConfigCallback(saveConfigCallback);
  wm.addParameter(&cst_eqpId);
  wm.addParameter(&cst_apiUrl);
  res = wm.autoConnect("Buri-Hardware","123456789");
  if(SPIFFS.begin(true)){
    Serial.println("LOG:Mounted");   
    File configFile = SPIFFS.open("/config.json","r");
    if(configFile){
      Serial.println("LOG:Opened");
      size_t size = configFile.size();
      std::unique_ptr<char[]> buf(new char[size]);
      configFile.readBytes(buf.get(),size);
      auto deserializeError = deserializeJson(json_config,buf.get());
      serializeJson(json_config,Serial); // Mostrado o json lido no serial (apenas pra verificação, comentar depois)
      if (!deserializeError){
        strcpy(id,json_config["id"]);
        strcpy(BASE_URL,json_config["url"]);
      } else {
        Serial.println("LOG:Failed to load info json"); 
      }
    } else {
      Serial.println("LOG:Failed to load json file");
    }
    configFile.close();
  } else {
    Serial.println("LOG:Failed to mount FS");
  }

  if(!res){
    Serial.println("LOG:Failed to connect Wifi");
  } else {
    Serial.println("LOG:Connected Wifi");
    timeClient.begin();  
  }

  if(should_save_config){
    strcpy(id,cst_eqpId.getValue());
    strcpy(BASE_URL,cst_apiUrl.getValue());
    Serial.println("LOG:Saving config");
    json_config["id"] = id;
    json_config["url"] = BASE_URL;
    File configFile = SPIFFS.open("/config.json","w");
    if(!configFile) {
      Serial.println("LOG:Failed to open file for writing");
    }
    serializeJson(json_config,Serial); // Mostrado o json lido no serial (apenas pra verificação, comentar depois)
    serializeJson(json_config,configFile);
    configFile.close();
  }
  dht_sensor.begin();
  mq7.calibrate();  
}

void loop() {
  delay(200);
  dht_values = dht_sensor.readHumidity();
  if(isnan(dht_values)){
    measurement["airH"] = nullptr;
  } else {
    measurement["airH"] = String(dht_values,2);
  }
  dht_values = dht_sensor.readTemperature();
  if(isnan(dht_values)){
    measurement["temp"] = nullptr;
  } else {
    measurement["temp"] = String(dht_values,2);
  }
  measurement["co"] = mq7.readPpm();

  if(mode_is_online){
    //Começo do código online
    digitalWrite(LED_PIN,HIGH);
    Serial.println("Log:Modo online");
    timeClient.update();
    
    String formattedDate = timeClient.getFormattedDate();
    String collectionDate = formattedDate.substring(0,formattedDate.length() -1) + "-04:00";
    Serial.println(collectionDate);
    measurement["collectionDate"] = collectionDate;
    measurement["equipmentId"] = String(id);
    serializeJson(measurement, measurementSerialized);
    if(wifiClient.connect(String(BASE_URL).substring(7).c_str(), 80)){
      Serial.println("Conectou no servidor da API");
      wifiClient.println("POST "+String(PATH_SAVE_MEASUREMENT)+" HTTP/1.0");
      wifiClient.println("Host: "+String(BASE_URL).substring(7));
      wifiClient.println("Content-Type: application/json");
      wifiClient.print("Content-Length: ");
      wifiClient.println(strlen(measurementSerialized.c_str()));
      wifiClient.println();
      wifiClient.println(measurementSerialized);
      while (wifiClient.connected()) {
        if (wifiClient.available()) {
          String response = wifiClient.readString();
          Serial.println("Resposta do servidor: ");
          Serial.println(response);
          wifiClient.stop();
        }
      }
    }

    //Fim do codigo online
  } else {
    //Começo do código offline
    measurement["collectionDate"] = "";
    digitalWrite(LED_PIN,LOW);
    Serial.println("Log:Modo offline");
    serializeJson(measurement, measurementSerialized);
    if (serial_bt.hasClient()) {
      Serial.println("LOG:Dispositivo Blueetooth conectado!");
      serializeJson(measurement, Serial);
      serial_bt.println(measurementSerialized);
      delay(100);
    } else {
      Serial.println("LOG:Aguardando conexão blueetooth ...");
      delay(100);
    }

    //Fim do codigo offline
  }
}