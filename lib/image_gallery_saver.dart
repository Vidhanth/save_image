import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/services.dart';

class ImageGallerySaver {
  static const MethodChannel _channel =
      const MethodChannel('save_image');

  static Future saveImage(Uint8List image, {int quality = 80, String name, String dirName}) async {
    assert(image != null);
    final result =
    await _channel.invokeMethod('saveImageToGallery', <String, dynamic> {
      'img': image,
      'quality': quality,
      'dirName': dirName,
      'name': name
    });
    return result;
  }

}
