
# coding: utf-8

# In[1]:


import numpy as np
from flask import Flask, request, jsonify
import pickle
from keras.models import load_model
import tensorflow


# In[3]:


model_url = "C:/Users/Sakib Mukter/Desktop/RiceProject/model1.h5"
#Keras Model
print("Loading Modedl")

model = load_model(model_url)
# graph = tensorflow.get_default_graph()
print("MODEL-LOADED")


# In[4]:


readDir = 'C:/Users/Sakib Mukter/Desktop/RiceProject/Segmented/CrowdAI-Train-Test/color/Apple___Apple_scab/'


readImage = readDir + '0b1e31fa-cbc0-41ed-9139-c794e6855e82___FREC_Scab 3089.JPG'


# In[5]:


import cv2
from keras.preprocessing.image import img_to_array
#Import in opencv 
IMG_SIZE = 256
img_array = cv2.imread(readImage) 
new_array = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE)) #RESIZE MAGES
img_array = img_to_array(new_array)

#Normalizing image numpy array
np_image_test = np.array(img_array, dtype=np.float16) / 225.0

#Expand dimension to predict the model in keras
np_image_test = np.expand_dims(np_image_test, axis=0)


# In[6]:


# model.summary()


# In[7]:


# global graph
# with graph.as_default():
#     #Predict image
print("prediction called")
a = model.predict(np_image_test)
  


# In[ ]:


# load Flask 
import flask
app = flask.Flask(__name__)


# In[9]:


# define a predict function as an endpoint 
@app.route("/predict", methods=["GET","POST"])
def predict():
    data = {"success": False}
    # get the request parameters
    params = flask.request.json
    if (params == None):
        params = flask.request.args
    # if parameters are found, echo the msg parameter 
    if (params != None):
        data["response"] = params.get("msg")
        data["success"] = True
    # return a response in json format 
    return flask.jsonify(data)
# start the flask app, allow remote connections
app.run(host='0.0.0.0')

