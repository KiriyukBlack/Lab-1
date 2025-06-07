// Import the functions you need from the SDKs you need
const { initializeApp } = require ( "firebase/app");
const { getFirestore } = require ( "firebase/firestore");
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBhswT-2d7sauakjV_D9FS9ikreBvWFFCI",
  authDomain: "cem-example-666b9.firebaseapp.com",
  projectId: "cem-example-666b9",
  storageBucket: "cem-example-666b9.firebasestorage.app",
  messagingSenderId: "993757662231",
  appId: "1:993757662231:web:652cf1f38536b4ec3f2052",
  measurementId: "G-4KXHCERPYD"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

module.ecports = {db};