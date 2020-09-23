const express = require('express')
const database = require('database')
const util = require('util')
const admin = require('firebase-admin')


const routes = express.Router()


const serviceAccount = require("./firebaseServiceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://partem-621b3.firebaseio.com"
})


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
  console.log("GOT POST register REQUEST")
  console.log(req.body)
  //console.log(database.query("SELECT * from user;"))
  const username = req.body.username
  const email = req.body.email
  const password = req.body.password
  database.mysqlConnection.query(`INSERT INTO userdb.user (username, email, password) VALUES ('${username}', '${email}','${password}');`,(err,result,fields)=>{
    if(!err){
      res.send("Successfully Registered You Retard")
      console.log("Row Added")
      console.log(result)
      //console.log("last id is: " + result.insertId)
    }else{
      if(err.errno == 1062){
        res.send("You're already registered dumbass.")
        console.log("Error Adding Row")
        //console.log(err.errno)
        //throw err
      }else{
        res.send("Couldn't register you for whatever goddamn reason I don't really care.")
        console.log("Error adding row")
      }
    }
  })

})

routes.get('/tos',(req,res)=>{
  res.send("This site is still under construction... retard")



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


routes.post('/firebase_token',(req,res)=>{
  const token = req.body.token
  const username = req.body.username
})

/*
routes.post('/create_group',async(req,res)=>{
  let userID
  let groupID
  const groupname = req.body.groupname
  const username = req.body.username
  console.log("username is: " + username)
  console.log("groupname is: " + groupname)

  const test1 = await database.mysqlConnection.query(`SELECT id FROM userdb.user WHERE username = '${username}';`,async (err,rows,fields)=>{
    userID = rows[0].id
    console.log("userID in function: " + userID)
    return userID
  })

  const test2 = await database.mysqlConnection.query(`INSERT INTO userdb.group (name) VALUES ('${groupname}');`,(err,results,fields)=>{
    groupID = results.insertId
    console.log("groupID in function: " + groupID)
    if(!err){
      res.send("Successfully Created Group Retard")
      console.log("Row Added to Group Table")
      return groupID
    }else{
        res.send("Couldn't create group for some stupid reason I don't know.")
        console.log("Error Adding Row")
        return 0
    }


  })

  console.log("userID is: " + test1)
  console.log("groupID is: " + test2)
  database.mysqlConnection.query(`INSERT INTO userdb.user2group (userID,groupID) VALUES ('${userID}','${groupID}');`,(err,results,fields)=>{
    if(!err){
      console.log("added to user2group table")
    }else{
      console.log("Couldnt add to user2group")
      console.log(err)
    }


  })


})
*/

routes.post('/create_group', async(req,res)=>{
  const members = req.body.members
  const groupname = req.body.groupname
  const username = req.body.username

  console.log("username is: " + username)
  console.log("groupname is: " + groupname)
  //console.log("members to add: " + members[0])

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
  res.status(404)
  res.send("This Shit don't exist yo!\nAlso, where's ma foty thousan?!")
})





module.exports = routes
