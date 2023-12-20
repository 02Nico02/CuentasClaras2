// Se crea para poder darle un tipo de dato específico
// al credentials que está en el servicio de request 
// deberían coincidir la cantidad de campos a enviar
export interface LoginRequest{
    email:string,
    password:string
}