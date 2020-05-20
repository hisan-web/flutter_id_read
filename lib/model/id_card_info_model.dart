import 'dart:typed_data';

class IdCardInfoModel {
  String certType;
  String peopleName;
  String idCard;
  String sex;
  Uint8List wltData;
  String base64Image;

  IdCardInfoModel({
    this.certType: "",
    this.peopleName: "",
    this.idCard: "",
    this.sex: "",
    this.wltData: null,
    this.base64Image: ""
  });

  factory IdCardInfoModel.fromJson(Map json) => IdCardInfoModel(
    certType: json["certType"] == null ? "" : json["certType"],
    peopleName: json["peopleName"] == null ? "" : json["peopleName"],
    idCard: json["idCard"] == null ? "" : json["idCard"],
    sex: json["sex"] == null ? "" : json["sex"],
    wltData: json["wltData"] == null ? null : json["wltData"],
    base64Image: json["base64Image"] == null ? "" : json["base64Image"]
  );

  Map<String, dynamic> toJson() => {
    "cityName": certType == null ? null : certType,
    "contactName": peopleName == null ? null : peopleName,
    "mobile": idCard == null ? null : idCard,
    "address": sex == null ? null : sex,
    "tag": wltData == null ? null : wltData,
    "base64Image": base64Image == null ? null : base64Image
  };
}