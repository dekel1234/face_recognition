def pre_process(face, required_size=(160, 160)):

    ret = cv2.resize(face, required_size)
    #ret = cv2.cvtColor(ret, cv2.COLOR_BGR2RGB)
    ret = ret.astype('float32')
    # standardize pixel values across channels (global)
    mean, std = ret.mean(), ret.std()
    ret = (ret - mean) / std

    return ret

def load_tflite_model(file):
    # Load the TFLite model and allocate tensors.
    interpreter = tf.lite.Interpreter(model_path=file)
    interpreter.allocate_tensors()
    return interpreter

def predict(face_model, samples):
    # Get input and output tensors.
    input_details = face_model.get_input_details()
    output_details = face_model.get_output_details()

    # Test the model on random input data.
    input_shape = input_details[0]['shape']

    #input_data = np.array(np.random.random_sample(input_shape), dtype=np.float32)
    outputs = []
    for sample in samples:
        input_data = sample.reshape(input_shape)
        #input_data = np.expand_dims(input_data, axis=0)
        face_model.set_tensor(input_details[0]['index'], input_data)
        face_model.invoke()
        # The function `get_tensor()` returns a copy of the tensor data.
        # Use `tensor()` in order to get a pointer to the tensor.
        output_data = face_model.get_tensor(output_details[0]['index'])
        #print(output_data)
        outputs.append(output_data)
    ret = np.stack(outputs)
    return ret


imgs = [bill1, bill2, larry]
samples = [pre_process(i) for i in imgs]

# load tfl model
tfl_file = "/content/keras-facenet/model/facenet.tflite"
tflite_model = load_tflite_model(tfl_file)

embeddings = predict(tflite_model, samples)

print("distance bill vs bill", np.linalg.norm(embeddings[0, :] - embeddings[1, :]))
print("distance bill vs larry", np.linalg.norm(embeddings[0, :] - embeddings[2, :]))