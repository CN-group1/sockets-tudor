# -*- coding: utf-8 -*-
import socket

# --- CONFIGURARE ---
# AICI TREBUIE PUS IP-UL DE ZEROTIER AL STUDENTULUI A (Serverul)
SERVER_IP = '100.70.154.105' 
PORT = 9090
BUFFER_SIZE = 1024

def start_tcp_chat_client():
    """ Client TCP Bidirecțional (Conexiune Fiabilă) """
    print("--- Client TCP Bidirecțional (Chat) ---")
    
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        try:
            s.connect((SERVER_IP, PORT))
            print(f"[+] Conectat la serverul TCP {SERVER_IP}:{PORT}")
            print("Tu începi conversația! (Scrie 'exit' pentru a ieși)\n")
            
            while True:
                # 1. Trimite mesaj
                mesaj = input("[Tu - Client TCP]: ")
                s.sendall(mesaj.encode('utf-8'))
                
                if mesaj.lower() == 'exit':
                    break
                    
                # 2. Așteaptă răspuns
                data = s.recv(BUFFER_SIZE)
                if not data:
                    print("\n[-] Serverul a închis conexiunea.")
                    break
                    
                print(f"[Server]: {data.decode('utf-8')}")
                
        except ConnectionRefusedError:
            print("[-] Eroare: Conexiunea TCP refuzată. Serverul e pornit?")

def start_udp_chat_client():
    """ Client UDP Bidirecțional (Fără Conexiune) """
    print("--- Client UDP Bidirecțional (Chat Ping-Pong) ---")
    
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
        print(f"[*] Pregătit pentru comunicare UDP cu {SERVER_IP}:{PORT}")
        print("Tu începi! (Scrie 'exit' pentru a ieși)\n")
        
        # Setează un timeout (ex: 10 secunde) ca să nu se blocheze așteptând la infinit
        # dacă pachetul UDP de răspuns se pierde pe rețea
        s.settimeout(10.0) 
        
        while True:
            # 1. Trimite mesaj
            mesaj = input("[Tu - Client UDP]: ")
            s.sendto(mesaj.encode('utf-8'), (SERVER_IP, PORT))
            
            if mesaj.lower() == 'exit':
                break
                
            # 2. Așteaptă răspuns
            try:
                data, addr = s.recvfrom(BUFFER_SIZE)
                print(f"[Server {addr[0]}]: {data.decode('utf-8')}")
            except socket.timeout:
                print("[-] Nu s-a primit niciun răspuns în 10 secunde (pachet pierdut?).")

if __name__ == "__main__":
    # --- SELECTOR DE PROTOCOL ---
    # Decomentează funcția dorită pentru a testa protocolul respectiv:
    
    #start_tcp_chat_client()
     start_udp_chat_client()