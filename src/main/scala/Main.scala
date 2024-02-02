
// https://github.com/samdjstevens/java-totp


import dev.samstevens.totp.code._

case class Secret( value: String) extends AnyVal
case class Digits( value: Int) extends AnyVal
case class Period( value: Int) extends AnyVal

val hashingAlgorithm = HashingAlgorithm.SHA512
val digits = Digits(6)
val period = Period(30)



@main def generationQR: Unit = {
    
    import scala.io.StdIn.readLine    
    
    def ui( run: Boolean, secret: Secret) : Unit =  
    
        if ( run )
            val code = readLine(" : Please Do you check your VerificationCode? ")
            if (validateCode( secret, code )) 
                println( "  :: Code OK!!!" )
            else 
                println (" :: Fail")
            ui( readLine(" : Continue? [Y/N] ").toUpperCase.trim == "Y" , secret ) 
                
        
    
    val secret = generateSecret
    
    println( s" ---> secret -> ${secret}" )
    
    writeFile( (generateQr( secret )) )
    
    ui( true, secret )
    
    
}

def generateSecret : Secret = 
    import dev.samstevens.totp.secret.DefaultSecretGenerator
    Secret( ( new DefaultSecretGenerator() ).generate() )

def generateQr( secret: Secret ) : String =
    import dev.samstevens.totp.qr.{QrData, ZxingPngQrGenerator}
    import dev.samstevens.totp.code.HashingAlgorithm
    import dev.samstevens.totp.util.Utils.getDataUriForImage
    val data = new QrData.Builder()
               .label("example@example.com")
               .secret(secret.value)
               .issuer("AppName")
               .algorithm(hashingAlgorithm) 
               .digits(digits.value)
               .period(period.value)
               .build();
    val uri = data.getUri()
    val generator = new ZxingPngQrGenerator();
    val imageData = generator.generate(data)
    val mimeType = generator.getImageMimeType();
    
    s"""
    | <html>
    |   <body>
    |    <h1>Scan your secret!!!!</h1>
    |    <img src="${getDataUriForImage(imageData, mimeType)}" />
    |    <div>
    |        <a href="${uri}">$uri<a>
    |    </div>
    |   </body>
    | </html>
    """.stripMargin

def writeFile( qrCode: String ): Unit = 
    import java.io._
    val pw = new PrintWriter(new File("qr.html" ))
    pw.write(qrCode)
    pw.close
    println( "## Generating QR in file: qr.html" )
    
def validateCode( secret: Secret, code: String ) : Boolean = 
    
    import dev.samstevens.totp.time._
    
    val timeProvider = new SystemTimeProvider() 
    val codeGenerator = new DefaultCodeGenerator(hashingAlgorithm, digits.value)
    val verifier = new DefaultCodeVerifier(codeGenerator, timeProvider)
    
    val currentBucket = Math.floorDiv(timeProvider.getTime(), period.value);
    
    println(s" ---> Generating code from library -> ${codeGenerator.generate( secret.value, currentBucket )}")

    verifier.isValidCode(secret.value, code) 
