//const express = require("express")
//const fs = require("fs")

const date = new Date()


function ipsend(req,res,next){

  console.log("\n\n------------------------NEW REQUEST------------------------")
  console.log("\nRequest type: " + req.method)
  console.log("\nRoute: " + req.originalUrl)
  console.log("\nIP from sender: " + req.ip)
  console.log("\nSent at: " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds())
  console.log("\nRequest info:\n")
  next()

}


module.exports = {ipsend}
