const express = require('express')
const database = require('database')
const util = require('util')
const firebase = require('./firebase.js')
const multer = require('multer')
const path = require('path')
const fs = require('fs')


const TESTOKEN = 'fZxFKPwkT7GhUkR-W7zUMe:APA91bF8n4F78gn4FjAiomzxthFSu5r4tAKVwQN5aOAnEfWzYF5JE_xETQ77f5sYETTIwR3t1tRr2NPTeno82fn4G5EoNr1ce6OrXIicqXKviMJBgQieVyAS8ce2YTMOQnfv2EhPPf8p'


const routes = express.Router()


//const serviceAccount = require("./firebaseServiceAccountKey.json");
/*
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://partem-621b3.firebaseio.com"
})

*/


routes.get('/database',(req,res)=>{
  console.log("GOT GET database REQUEST")
  //console.log(database.query("SELECT * from user;"))
  database.mysqlConnection.query("SELECT * from user",(err,rows,fields)=>{
    if(!err){
      res.send(rows)
    }else
      res.send("")
  })



  /*
  database.query("SELECT * from user").then((results)=>{
    res.send(results)
  }).catch((err)=>{
    console.log(err)

  })
  */
})



routes.post('/register',(req,res)=>{
  //console.log("GOT POST register REQUEST")
  console.log(req.body)
  //console.log(database.query("SELECT * from user;"))
  const username = req.body.username
  const email = req.body.email
  const password = req.body.password
  database.mysqlConnection.query(`INSERT INTO userdb.user (username, email, password) VALUES ('${username}', '${email}','${password}');`,(err,result,fields)=>{
    if(!err){
      res.send("Successfully Registered You Retard")
      console.log("ROW ADDED")
      console.log(result)
      //console.log("last id is: " + result.insertId)
    }else{
      if(err.errno == 1062){
        res.status(401)
        res.send("You're already registered dumbass.")
        console.log("Error Adding Row: Duplicate user")
        console.log(err)
        //throw err
      }else{
        res.status(400)
        res.send("Couldn't register you for whatever goddamn reason I don't really care.")
        console.log("Error adding row")
      }
    }
  })

})

routes.get('/tos',(req,res)=>{
  res.send("This site is still under construction... retard")



})

const upload = multer({
  dest: path.join(__dirname,"./uploads")
})

routes.post('/upload',upload.single('image'),(req,res)=>{
  const username = req.body.username
  const tempPath = req.file.path
  const ext = req.file.originalname
  console.log("filename is: " + ext)
  //console.log("username is: " + username)
  //console.log(tempPath)
  const targetPath = path.join(__dirname,`./uploads/${ext}`)

  //console.log(req.file)

  fs.rename(tempPath,targetPath,err=>{
    if (err){
      console.log("There was an error!!")
    }else{
      res.status(200)
      res.send("Successfully uploaded yo ugly ass mug.")
      console.log("image upload successful!")
    }
  })




})


routes.get('/login',(req,res)=>{

  const username = req.query.username
  const password = req.query.password
  const amountOwed = 30
  const amountSpent = 20
  //console.log(username)
  //console.log(password)

  database.mysqlConnection.query(`SELECT id FROM userdb.user WHERE username = '${username}' AND password = '${password}';`,(err,rows,fields)=>{

    //console.log(err)
    if(rows.length == 1){
      res.send({
        amountOwed,
        amountSpent
      })
      console.log(res.statusCode)

      console.log("Yo bitchass ID is: " + rows[0].id)
    }else{
      res.status(401)
      res.send("you ain't registered biatch.")
      console.log("you ain't registered biatch.")
    }

  })

})

//Fix this shit
routes.post('/firebase_token',(req,res)=>{
  const token = req.body.token
  const username = req.body.username
  console.log("got that firebase")
  console.log("token is: " + token)
  console.log("username is: " + username)

  database.mysqlConnection.query(`UPDATE userdb.user SET devToken = '${token}' WHERE username = '${username}';`,(err,result,fields)=>{
    if(err){
      console.log("Couldnt update")
    }else{
      console.log("updated that bitch")
      res.status(200)
      res.send("Updated device token")
    }
  })



})



routes.post('/group_invite',async (req,res)=>{
  const userFrom = req.body.userFrom
  const userTo = req.body.userTo
  const group = req.body.group
  const token = req.body.token
  console.log("user to be invited: " + userTo)
  console.log("group to be invited: " + group)
  console.log("token is: " + token)

  const userQ = await database.query(`SELECT devToken FROM userdb.user WHERE username = '${userTo}';`)
  if(userQ.length == 1){
    const token = userQ[0].devToken
    //console.log("token is: " + token)

    const message = {
    data: {
      type: '0',
      title: 'Group Invite',
      text: `SOME RETARD, ${userFrom}, INVITED YOU TO JOIN THE GROUP, ${group}, BITCH!`,
      userFrom: `${userFrom}`,
      userTo: `${userTo}`,
      group: `${group}`
    },
    token: token
    }

    // Send a message to the device corresponding to the provided
    // registration token.
    firebase.messaging().send(message)
      .then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response)
        res.status(200)
        res.send("invite sent")
      })
      .catch((error) => {
        console.log('Error sending message:', error)
      })
    }else{
      console.log("USER DONT EXIST")
      res.status(404)
      res.send("user dont exist")
    }



})

routes.post('/accept_group_inv', async(req,res)=>{
  const username = req.body.username
  const group = req.body.group

  const userQ = await database.query(`SELECT id FROM userdb.user WHERE username = '${username}';`,(err)=>{
    if(err){
      console.log(err)
    } else{
      console.log("User found!")
    }
  })
  const userID = userQ[0].id

  const groupQ = await database.query(`SELECT id FROM userdb.user WHERE username = '${username}';`,(err)=>{
    if(err){
      console.log(err)
    } else{
      console.log("Group found!")
    }
  })
  const groupID = groupQ[0].id

  const user2groupQ = database.query(`INSERT INTO userdb.user2group (userID,groupID) VALUES ('${userID}','${groupID}');`,(err,results,fields)=>{
    if(!err){
      console.log("User added to group.")
      res.send("Added yo bitchass to the group")
    }else{
      console.log("could not add use to group")
      res.send("Couldn't add yo bitchass to the group!")
    }
  })


})



routes.post('/create_group', async(req,res)=>{
  const members = req.body.members
  const groupname = req.body.groupname
  const username = req.body.username

  console.log("username is: " + username)
  console.log("groupname is: " + groupname)
  console.log("members to add: " + members[0])

  const userQ = await database.query(`SELECT id FROM userdb.user WHERE username = '${username}';`)
  const userID = userQ[0].id
  console.log("userID is: " + userID)

  const groupQ = await database.query(`INSERT INTO userdb.group (name) VALUES ('${groupname}');`)
  const groupID = groupQ.insertId
  console.log("groupID is: " + groupID)

  const user2groupQ = database.query(`INSERT INTO userdb.user2group (userID,groupID) VALUES ('${userID}','${groupID}');`,(err,results,fields)=>{
    if(!err){
      console.log("Row added to group table.")
      res.send("Group Created Bitch")
    }else{
      console.log("could not add row to group table")
      res.send("Group Creation Failed Retard!")
    }
  })

/*
  const userQ = await database.query(`SELECT devToken FROM userdb.user WHERE username = '${username}';`)
  if(userQ.length == 1){
    const token = userQ[0].devToken
    console.log("token is: " + token)

    const message = {
    data: {
      topic: 'YOU GOT AN INVITE BITCH',
      amount: '69'
    },
    token: token
    }

    // Send a message to the device corresponding to the provided
    // registration token.
    firebase.messaging().send(message)
      .then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response)
      })
      .catch((error) => {
        console.log('Error sending message:', error)
      })
    }else{
      console.log("USER DONT EXIST")
      res.status(404)
      res.send("user dont exist")
    }
*/

})





routes.get('/',(req,res) => {
    res.send('Hello Losers')

    console.log('GOT GET root REQUEST')

})

routes.get("/client_token", (req, res) => {
  console.log("Got client token request")
  gateway.clientToken.generate({}, (err, response) => {
    res.send(response.clientToken)
  })
})


routes.post("/checkout", (req, res) => {


  const paymentMethodNonce = req.body.nonce
  const amount = req.body.amount
  const deviceData = req.body.deviceData
  console.log("Nonce is: ",paymentMethodNonce)
  console.log("amount is: ", amount)

  //res.send("Got that stupid ass nonce")

/*

  gateway.transaction
  .sale({
    amount,
    paymentMethodNonce,
    options: { submitForSettlement: true },
  })
  .then((result) => {
    const { success, transaction } = result

    return new Promise((resolve, reject) => {
      if (success || transaction) {
        //res.redirect(`checkouts/${transaction.id}`);
        res.send(result)

        resolve();
      }

      reject(result);
    })
  })
  .catch(({ errors }) => {
    const deepErrors = errors.deepErrors();

    debug('errors from transaction.sale %O', deepErrors);

    req.flash('error', { msg: formatErrors(deepErrors) });
    res.redirect('checkouts/new');
  })

*/

  gateway.transaction.sale({
    amount: amount,
    paymentMethodNonce: paymentMethodNonce,
    deviceData: deviceData,
    options: {
      submitForSettlement: true
    }
  }, function(err, result) {
    if(!result.success || err){
      console.log("Transaction Failed you Retard.")
      res.send("Transaction Failed you Retard.")
    } else{
      console.log("Transaction Complete You Bitchass Mothafucka!")
      res.send("Transaction Complete You Bitchass Mothafucka!")
    }
    console.log(typeof(result))
    console.log(result.success)

  // Use payment method nonce here
  })


  routes.post('/data', (req,res) => {
      const data = req.body
      console.log(data.amount)
      console.log(data.groupID)
      res.send('got that shit')

  })


})

routes.get('/bitch',(req,res) => {
  res.send('you suck')
})

routes.get('/emily',(req,res) => {
    res.sendFile('emily.html',{root: "../Client"})
    console.log('GOT GET emily REQUEST')

})

routes.get('*',(req,res)=>{
  console.log("BAD REQUEST")
  res.status(404)
  res.send("This Shit don't exist yo!\nAlso, where's ma foty thousan?!")
})





module.exports = routes
