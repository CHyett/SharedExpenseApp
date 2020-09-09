const braintree = require('braintree')
const { Transaction } = require('braintree')
const https = require('https');
const fs = require('fs');
const express = require('express');
const path = require('path');
const bodyParser = require("body-parser");
const routes = require("./routes.js")
let requests = 0

const app = express();
const port = 443;
app.set('trust proxy', true)
app.use(express.static(path.join("../Client/images")))
app.use('/',routes)
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))




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







// start the server

https.createServer({
  key: fs.readFileSync('privkey.pem'),
  cert: fs.readFileSync('fullchain.pem')
}, app).listen(port, function () {
  console.log(`Server is running on port: ${port}`);
})


//module.exports = mysqlConnection