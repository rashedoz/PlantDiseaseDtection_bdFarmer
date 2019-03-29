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

model = load_model("N:/RiceDetectionGithub/tfModels/model1.h5")
graph = tensorflow.get_default_graph()
print("MODEL-LOADED")

Last_id = -1
print("Last id = %d" % Last_id)


  
from flask import Flask, request, jsonify, render_template

# creates a Flask application, named app
app = Flask(__name__)

# a route where we will display a welcome message via an HTML template
@app.route("/")
def hello():

    CATEGORIES = ['Apple___Apple_scab','Apple___Black_rot','Apple___Cedar_apple_rust',
     'Apple___healthy','Blueberry___healthy',
     'Cherry_(including_sour)___Powdery_mildew',
     'Cherry_(including_sour)___healthy',
     'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
     'Corn_(maize)___Common_rust_', 'Corn_(maize)___Northern_Leaf_Blight',
     'Corn_(maize)___healthy', 'Grape___Black_rot',
     'Grape___Esca_(Black_Measles)',
     'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)', 'Grape___healthy',
     'Orange___Haunglongbing_(Citrus_greening)', 'Peach___Bacterial_spot',
     'Peach___healthy', 'Pepper,_bell___Bacterial_spot',
     'Pepper,_bell___healthy', 'Potato___Early_blight', 'Potato___Late_blight',
     'Potato___healthy', 'Raspberry___healthy', 'Soybean___healthy',
     'Squash___Powdery_mildew', 'Strawberry___Leaf_scorch',
     'Strawberry___healthy', 'Tomato___Bacterial_spot', 'Tomato___Early_blight',
     'Tomato___Late_blight', 'Tomato___Leaf_Mold', 'Tomato___Septoria_leaf_spot',
     'Tomato___Spider_mites Two-spotted_spider_mite', 'Tomato___Target_Spot',
     'Tomato___Tomato_Yellow_Leaf_Curl_Virus', 'Tomato___Tomato_mosaic_virus',
     'Tomato___healthy']

     #Firebase app initialization
    firebase_admin.get_app()
    # As an admin, the app has access to read and write all data, regradless of Security Rules
    ref = db.reference('last_entry')
    url_ref = db.reference('last_url')
    prediction_ref = db.reference('prediction')
    last_name_ref = db.reference('last_name')
    last_done_ref = db.reference('last_done')

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

        #Import in opencv 
        img = cv2.imread(write_url)
        a = img.shape
        print(a)


        contour_size = f1.DetectLeaf(img)

        print(contour_size)

        # hue sat value -hsv
        hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        IMG_SIZE = 256
        new_array = cv2.resize(hsv, (IMG_SIZE, IMG_SIZE)) #RESIZE MAGES
        img = new_array

        #Green upper and lowerbound
        lower_green = np.array([36,0,0])
        upper_green = np.array([86,255,255])
        mask = cv2.inRange(img, lower_green, upper_green)
        res = cv2.bitwise_and(img,img, mask= mask)

        ratio_brown = cv2.countNonZero(mask)/(img.size/3)
        ratio_percentage = np.round(ratio_brown*100, 2)
        print('green pixel percentage:', ratio_percentage)

        if ratio_percentage < 20:
            print("No leaf detected")
            return render_template('no-img.html')

        elif ratio_percentage >= 20:
            img_array = img_to_array(new_array)
            #Normalizing image numpy array
            np_image_test = np.array(img_array, dtype=np.float16) / 225.0

            #Expand dimension to predict the model in keras
            np_image_test = np.expand_dims(np_image_test, axis=0)

            global graph
            with graph.as_default():
                #Predict image
                print("prediction called")
                a = model.predict(np_image_test)

                
                pred_list = a
                pred_list = np.around(pred_list, decimals=2)
                
                pred_list.sort()
                pred_single = pred_list[0]

                pred_single[:] = pred_single[::-1]

                result_pb = pred_single[:3]
                
                print("pred_single=",pred_single)

                a = model.predict_classes(np_image_test)
                
                result = CATEGORIES[int(a)]
                prediction_result = 'Image prediction - '+ result

                print(prediction_result)

            # plt.imshow(new_array)
            # plt.show()
            # img_array

            prediction_ref.set({
                'pred': result
                })


            

            # with open(write_segmented, "rb") as f:
            #   str1 = base64.b64encode(f.read())
            #   # print(str1)

            

            #Plotting Images
            # plt.subplot(131),plt.imshow(img,cmap = 'gray'),plt.title("img")
            # plt.xticks([]), plt.yticks([])
            # plt.subplot(132),plt.imshow(mask,cmap = 'gray'),plt.title('mask')
            # plt.xticks([]), plt.yticks([])
            # plt.subplot(133),plt.imshow(res,cmap = 'gray'),plt.title('res')
            # plt.xticks([]), plt.yticks([])
            # plt.show(block=False)
            # plt.pause(2)
            # plt.close('all')
            
            

            last_name_ref.set(image_url)
            last_done_ref.set(last_entry)

            datas = {'a': a,"prediction":prediction_result,
                    "image_url":image_url,"pred_list":result_pb}


            return render_template('img_processor.html', **datas)

    else :
        print("No new image Last id = %d" % Last_id)
        return render_template('no-img.html')



    





if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5000)

