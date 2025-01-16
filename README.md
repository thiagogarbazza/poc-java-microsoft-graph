# poc-java-microsoft-graph
POC Microsoft Graph with java

## Variáveis importantes

1. `TENANT_ID`: ID do diretório (locatáro)
1. `CLIENT_ID`: ID do aplicativo (cliente)
1. `CLIENT_SECRET`: Segredo (senha) do cliente para acesso via API.
1. `SITE_ID`: ID do site (sharepoint)

## Conceder permissão Sites.Selected

Para a permissão "funcionar" é preciso conceder a permissão de leitura/escrita ao APP.
1. Tem que ser dado a permissão "Sites.FullControl.All" para o aplicativo.
1. Depois disso tem que chamar uma API para dar permissão no site ao aplicativo.
   ```js
   const URL = `https://graph.microsoft.com/v1.0/sites/${SITE_ID}/permissions`
   const HEADERS = {
     "Authorization": `Bearer ${TOKEN}`
   }
   const BODY = {
     "roles": ["write"],
     "grantedToIdentities": [
       {
         "application": {
           "id": "${APP_ID}",
           "displayName": "${APP_NAME}"
         }
       }
    ]
   }
   ```
1. Remove a permissão "Sites.FullControl.All" para o aplicativo.

Observação: processo resumido neste texto do GIT https://gist.github.com/ruanswanepoel/14fd1c97972cabf9ca3d6c0d9c5fc542