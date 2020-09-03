const express = require('express');


const routes = express.Router()



routes.get('/bitch',(req,res) => {
  res.send('you suck')
})

routes.get('/emily',(req,res) => {
    res.sendFile('emily.html',{root: __dirname})
    console.log('GOT GET emily REQUEST')
    console.log("IP from sender is",req.ip)

})



module.exports = routes
