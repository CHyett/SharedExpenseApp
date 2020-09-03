const braintree = require('braintree')
const { Transaction } = require('braintree')
const https = require('https');
const fs = require('fs');
const express = require('express');
const path = require('path');
const bodyParser = require("body-parser");
const routes = require("./routes.js")
var requests = 0

const app = express();
const port = 443;
app.set('trust proxy', true)
app.use(express.static(path.join(__dirname)))


const TRANSACTION_SUCCESS_STATUSES = [
  Transaction.Status.Authorizing,
  Transaction.Status.Authorized,
  Transaction.Status.Settled,
  Transaction.Status.Settling,
  Transaction.Status.SettlementConfirmed,
  Transaction.Status.SettlementPending,
  Transaction.Status.SubmittedForSettlement,
];

function formatErrors(errors) {
  let formattedErrors = '';

  for (let [, { code, message }] of Object.entries(errors)) {
    formattedErrors += `Error: ${code}: ${message}
`;
  }

  return formattedErrors;
}

function createResultObject({ status }) {
  let result;

  if (TRANSACTION_SUCCESS_STATUSES.indexOf(status) !== -1) {
    result = {
      header: 'Sweet Success!',
      icon: 'success',
      message:
        'Your test transaction has been successfully processed. See the Braintree API response and try again.',
    };
  } else {
    result = {
      header: 'Transaction Failed',
      icon: 'fail',
      message: `Your test transaction has a status of ${status}. See the Braintree API response and try again.`,
    };
  }

  return result;
}


const gateway = braintree.connect({
  environment: braintree.Environment.Sandbox,
  merchantId: "ztyj4ktc4wmv4tp3",
  publicKey: "y3yvv9bc8y5pyysv",
  privateKey: "8373b55587b7e1d7eaa2b8c91a96e22c"
})



app.use('/',routes)
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

app.get('/',(req,res) => {
    requests+=1
    res.send('Hello Losers')

    console.log('GOT GET root REQUEST')
    console.log("IP from sender is",req.ip)

})


app.get("/client_token", (req, res) => {
  console.log("Got client token request")
  console.log("IP from sender is",req.ip)
  gateway.clientToken.generate({}, (err, response) => {
    res.send(response.clientToken)
  })
})


app.post("/checkout", (req, res) => {
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



})





app.post('/data', (req,res) => {
    const data = req.body
    console.log(data.amount)
    console.log(data.groupID)
    res.send('got that shit')

})

// start the server

https.createServer({
  key: fs.readFileSync('privkey.pem'),
  cert: fs.readFileSync('fullchain.pem')
}, app).listen(port, function () {
  console.log(`Server is running on port: ${port}`);
})


//module.exports = gateway
