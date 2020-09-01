const express = require('express');


const routes = express.Router()



routes.get('/bitch',(req,res) => {
  res.send('you suck')
})


module.exports = routes
