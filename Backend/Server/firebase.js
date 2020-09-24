const admin = require('firebase-admin')

const serviceAccount = require("./firebaseServiceAccountKey.json")

const init = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://partem-621b3.firebaseio.com"
})


module.exports = init
