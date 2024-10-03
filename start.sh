#!/bin/sh
# Iniciar WebApp en segundo plano
java -cp /app/myapp.jar ar.edu.utn.dds.k3003.app.WebApp &

# Iniciar HeladeraWorker
java -cp /app/myapp.jar ar.edu.utn.dds.k3003.app.HeladeraWorker

# Esperar a que cualquiera de los procesos finalice
wait -n

# Salir con el código de estado del proceso que terminó primero
exit $?
