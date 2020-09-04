const express = require('express')
const database = require('database')


const routes = express.Router()



routes.get('/database',(req,res)=>{
  console.log("GOT GET database REQUEST")
  //console.log(database.query("SELECT * from user;"))
  database.mysqlConnection.query("SELECT * from user",(err,rows,fields)=>{
    if(!err)
      res.send(rows[0])
    else
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






routes.get('/',(req,res) => {
    res.send('Hello Losers')

    console.log('GOT GET root REQUEST')
    console.log("IP from sender is",req.ip)

})

routes.get("/client_token", (req, res) => {
  console.log("Got client token request")
  console.log("IP from sender is",req.ip)
  gateway.clientToken.generate({}, (err, response) => {
    res.send(response.clientToken)
  })
})


routes.post("/checkout", (req, res) => {
  console.log("Received checkout get request")
  console.log("IP from sender is",req.ip)
  let transactionErrors
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
    console.log("IP from sender is",req.ip)

})






module.exports = routes
