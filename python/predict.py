import os
import sys
import numpy as np

# Suppress TensorFlow and absl logs
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # Suppress low-level TF logs
import logging
logging.getLogger('absl').setLevel(logging.ERROR)

import tensorflow as tf
tf.get_logger().setLevel('ERROR')

from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img, img_to_array

IMG_SIZE = 150

def load_model_safe():
    model_path = os.path.join("models", "cnn_model.h5")
    if not os.path.exists(model_path):
        print(f"Error: Model file not found at {model_path}", file=sys.stderr)
        sys.exit(1)
    try:
        return load_model(model_path)
    except Exception as e:
        print(f"Error loading model: {str(e)}", file=sys.stderr)
        sys.exit(1)

def predict(image_path):
    if not os.path.exists(image_path):
        print(f"Error: Image not found at {image_path}", file=sys.stderr)
        sys.exit(1)

    try:
        img = load_img(image_path, target_size=(IMG_SIZE, IMG_SIZE), color_mode='grayscale')
        img_array = img_to_array(img) / 255.0
        img_array = np.expand_dims(img_array, axis=0)
        prediction = model.predict(img_array, verbose=0)[0][0]
        return "yes" if prediction > 0.5 else "no"
    except Exception as e:
        print(f"Error during prediction: {str(e)}", file=sys.stderr)
        sys.exit(1)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python predict.py <image_path>", file=sys.stderr)
        sys.exit(1)

    image_path = sys.argv[1]
    model = load_model_safe()
    result = predict(image_path)
    print(result)
