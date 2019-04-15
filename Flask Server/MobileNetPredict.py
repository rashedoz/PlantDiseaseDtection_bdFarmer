from __future__ import division
import numpy as np
import cv2
import numpy as np
from matplotlib import pyplot as plt
import os
from keras.preprocessing.image import img_to_array
import time
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import requests
import tensorflow
from keras.models import load_model
import functions as f1
from keras.applications.mobilenet import preprocess_input
from keras.preprocessing import image
import keras




BASE_DIR = os.getcwd()
BASE_DIR = BASE_DIR.replace("\\","/")
print(BASE_DIR)

sJson = BASE_DIR + "/diseasedetect-e39f6-6d1ebab30232.json"
print(sJson)

# Fetch the service account key JSON file contents
cred = credentials.Certificate(sJson)
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://diseasedetect-e39f6.firebaseio.com/'
})

ff = "Root level"
print(ff)

#Keras Model
print("Loading Modedl")

model = load_model("model(MobileNet).hdf5")   #load_model("/home/mazumder_8100/crowdAi/models/model(MobileNet).hdf5")

graph = tensorflow.get_default_graph()
print("MODEL-LOADED")

Last_id = -1
print("Last id = %d" % Last_id)


labels = {0: 'অ্যাপল__স্ক্যাব',
    1: 'অ্যাপল__ব্লাক_রট',
    2: 'অ্যাপল__সিডার_অ্যাপল_আরস্ট',
    3: 'অ্যাপল__সুস্থ',
    4: 'ভূট্রা__ছারকোসপোরা_লিফ_স্পট',
    5: 'ভূট্রা__কমন_ রাস্ট',
    6: 'ভূট্রা__নর্থদান_লেছ_ব্লাইড',
    7: 'ভূট্রা__সুস্থ',
    8: 'আঙ্গুর__ব্লাক_রট',
    9: 'আঙ্গুর__এসকা',
    10: 'আঙ্গুর__লিফ_ব্লাইড',
    11: 'আঙ্গুর__সুস্থ',
    12: '!__পাতা নেই ',
    13: 'আলু__আরলি_ব্লাইড',
    14: 'আলু__লেট_ব্লাইড',
    15: 'আলু__সুস্থ',
    16: 'টমেটো__ব্যাকটেরিয়াল_স্পট',
    17: 'টমেটো__আরলি_ব্লাইড',
    18: 'টমেটো__লেট_ব্লাইড',
    19: 'টমেটো__লিফ_মল্ড',
    20: 'টমেটো__সেপটোরিয়া_লিফ_স্পট',
    21: 'টমেটো__স্পাইডার_মাইট',
    22: 'টমেটো__র্টাগেট_স্পট',
    23: 'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
    24: 'টমেটো__ইয়লো_লিফ',
    25: 'টমেটো__সুস্থ'}

labels_english = {0: 'Apple___Apple_scab',
    1: 'Apple___Black_rot',
    2: 'Apple___Cedar_apple_rust',
    3: 'Apple___healthy',
    4: 'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
    5: 'Corn_(maize)___Common_rust_',
    6: 'Corn_(maize)___Northern_Leaf_Blight',
    7: 'Corn_(maize)___healthy',
    8: 'Grape___Black_rot',
    9: 'Grape___Esca_(Black_Measles)',
    10: 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)',
    11: 'Grape___healthy',
    12: 'Not__Leaf',
    13: 'Potato___Early_blight',
    14: 'Potato___Late_blight',
    15: 'Potato___healthy',
    16: 'Tomato___Bacterial_spot',
    17: 'Tomato___Early_blight',
    18: 'Tomato___Late_blight',
    19: 'Tomato___Leaf_Mold',
    20: 'Tomato___Septoria_leaf_spot',
    21: 'Tomato___Spider_mites Two-spotted_spider_mite',
    22: 'Tomato___Target_Spot',
    23: 'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
    24: 'Tomato___Tomato_mosaic_virus',
    25: 'Tomato___healthy'}


 #Image Preprocessing
def prepare_image(file):
    img_path = ''
    img = image.load_img(img_path + file, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

def MakePred(image_dir):
    #Prepare and Predict Image
    preprocessed_image = prepare_image(image_dir)
    predictions = model.predict(preprocessed_image)
    
    #Predicted class indicies
    predicted_class_indices=np.argmax(predictions,axis=1)
    predicted_class_indices
    
#     print(labels[int(predicted_class_indices)])
    return str(labels[int(predicted_class_indices)]), predictions

  
from flask import Flask, request, jsonify, render_template

# creates a Flask application, named app
app = Flask(__name__)

# a route where we will display a welcome message via an HTML template
@app.route("/")
def hello():

   


     #Firebase app initialization
    firebase_admin.get_app()
    # As an admin, the app has access to read and write all data, regradless of Security Rules
    ref = db.reference('last_entry')
    url_ref = db.reference('last_url')
    prediction_ref = db.reference('prediction')
    last_name_ref = db.reference('last_name')
    last_done_ref = db.reference('last_done')
    top_4_ref = db.reference('prediction/top_4')

    global Last_id
    image_url = url_ref.get()
    last_entry =ref.get()
    # last_name = last_name_ref.get()

    if(Last_id==-1 or last_entry!=Last_id):
        Last_id = last_entry
        print("Last id = %d" % Last_id)
        # print(image_url)
        
    
        #readDir = 'N:/Rice Detection/PlantVillage CrodAi-Labeled/PlantVillage-Dataset/raw/color/Potato___healthy/'
        writeDir = BASE_DIR + '/Image Resources/'

        #readImage = readDir + '00fc2ee5-729f-4757-8aeb-65c3355874f2___RS_HL 1864.JPG'

        #Download and saving image

        timestr = time.strftime("%Y%m%d-%H%M%S")
        write_url = writeDir +timestr+ '.jpg'
        f = open(write_url,'wb')
        f.write(requests.get(image_url).content)
        f.close()


        global graph
        with graph.as_default():
            
            result, prediction_list= MakePred(write_url)

            #Get largest predictions
            print(prediction_list)
            p_l = prediction_list[0]
            pred_list = np.around(prediction_list, decimals=2)
            indexes_top_4 = np.argpartition(p_l, -4)[-4:] 
            print(indexes_top_4)
            top_4_names = []
            for x in indexes_top_4:
                name = str(labels_english[int(x)])
                print("N=",name,x)
                top_4_names.append(name)
            print(top_4_names)

            prediction_result = 'Image prediction - '+ result
            print(prediction_result)


        prediction_ref.set({
            'pred': result
            })


        last_name_ref.set(image_url)
        last_done_ref.set(last_entry)
        top_4_ref.set(top_4_names)

        datas = {"prediction":prediction_result,
                "image_url":image_url}

        return render_template('img_processor.html', **datas)

    else :
        print("No new image Last id = %d" % Last_id)
        return render_template('no-img.html')



    





if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5000)

