import mysql.connector
import numpy as np
import cv2
from deepface import DeepFace
import base64
from flask import Flask, request, jsonify

# Créer l'application Flask
app = Flask(__name__)

# Connexion à la base de données MySQL
def get_user_image_from_db(username):
    conn = mysql.connector.connect(
        host='localhost',
        user='root',  # Ton utilisateur MySQL
        password='',  # Ton mot de passe MySQL
        database='tache user'  # Ton nom de base de données
    )
    cursor = conn.cursor()
    query = "SELECT profile_image FROM user WHERE mail = %s"  # Utiliser l'email pour identifier l'utilisateur
    cursor.execute(query, (username,))
    result = cursor.fetchone()
    conn.close()

    if result:
        return result[0]  # Récupère l'image sous forme de BLOB
    else:
        return None

# Convertir une image Base64 en format OpenCV
def base64_to_image(base64_string):
    image_data = base64.b64decode(base64_string)
    np_arr = np.frombuffer(image_data, np.uint8)
    return cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

# Fonction de comparaison des visages
def compare_faces(username, captured_image_base64):
    try:
        # Convertir l'image capturée (base64) en image OpenCV
        image1 = base64_to_image(captured_image_base64)

        # Charger l'image de la base de données
        user_image_blob = get_user_image_from_db(username)
        if user_image_blob is None:
            return {"error": "Utilisateur non trouvé dans la base de données"}

        # Convertir le BLOB en image OpenCV
        nparr = np.frombuffer(user_image_blob, np.uint8)
        image2 = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        # Sauvegarder temporairement les images pour DeepFace
        cv2.imwrite("captured_image.jpg", image1)
        cv2.imwrite("profile_image.jpg", image2)

        # Comparer les visages avec DeepFace
        result = DeepFace.verify("captured_image.jpg", "profile_image.jpg", model_name='VGG-Face')

        if result["verified"]:
            return {"match": True, "message": "Connexion réussie"}
        else:
            return {"match": False, "message": "Échec de la reconnaissance faciale"}

    except Exception as e:
        return {"error": str(e)}

# Route pour effectuer la comparaison des visages via une API Flask
@app.route('/compare_faces', methods=['POST'])
def compare_faces_api():
    try:
        # Récupérer les données envoyées dans la requête
        data = request.get_json()
        username = data['username']
        captured_image_base64 = data['image1']

        # Appeler la fonction de comparaison
        result = compare_faces(username, captured_image_base64)

        # Retourner le résultat en format JSON
        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == '__main__':
    # Démarrer le serveur Flask
    app.run(debug=True, host='0.0.0.0', port=5000)
