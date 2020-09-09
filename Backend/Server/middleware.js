//const express = require("express")
//const fs = require("fs")

function ipsend(req,res,next){

  console.log("-----NEW REQUEST-----")
  console.log("Request type: " + req.method)
  console.log("Route: " + req.originalUrl)
  console.log("IP from sender: " + req.ip)
  next()

}


module.exports = {ipsend}
