const braintree = require('braintree')
const https = require('https');
const fs = require('fs');
const express = require('express');
const path = require('path');
const bodyParser = require("body-parser");
const routes = require("./routes.js")

const app = express();
const port = 443;

const gateway = braintree.connect({
  environment: braintree.Environment.Sandbox,
  merchantId: "ztyj4ktc4wmv4tp3",
  publicKey: "y3yvv9bc8y5pyysv",
  privateKey: "8373b55587b7e1d7eaa2b8c91a96e22c"
});



app.use('/',routes)
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))

app.get('/',(req,res) => {
    res.send('hello losers')
    console.log('GOT REQUEST')

})

app.get("/client_token", (req, res) => {
  console.log("Got client token request")
  gateway.clientToken.generate({}, (err, response) => {
    res.send(response.clientToken)
  })
})

app.post("/checkout", (req, res) => {
  const nonceFromTheClient = req.body.payment_method_nonce
  // Use payment method nonce here
})

app.get('/test', (req, res) => {
    //console.log(req.query.msg)
	  res.sendFile('test.txt',{root: __dirname})
    //const num = parseFloat(req.query.msg)
    //const result = num + 69
    //res.send(result.toString())
    console.log('GOT GET REQUEST')
})



app.post('/data', (req,res) => {
    const data = req.body
    console.log(data.amount)
    console.log(data.groupID)
    res.send('got that shit')

})

// start the server

https.createServer({
  key: fs.readFileSync('key2.pem'),
  cert: fs.readFileSync('cert2.pem')
}, app).listen(port, function () {
  console.log(`Server is running on port: ${port}`);
})


/*
app.listen(port, () => {
    console.log(`Server is running on port: ${port}`);
});
*/

//module.exports = gateway
