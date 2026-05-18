# ♻️ RecyClash

> Jogo 2D em Java sobre reciclagem: classifique os resíduos antes que eles caiam no chão.

**RecyClash** é um jogo arcade em Java/Swing. O jogador controla um coletor na parte inferior da tela e precisa interceptar apenas os resíduos **recicláveis** que descem do topo, evitando os **não recicláveis**. A dificuldade aumenta progressivamente conforme o tempo passa.

---

## 🎮 Como jogar

### Controles

| Ação | Comando |
|------|---------|
| Mover para a esquerda | `←` ou `A` |
| Mover para a direita | `→` ou `D` |
| Mover livremente | Movimento do mouse |
| Pausar / Retomar | `ESC` |
| Voltar ao menu (durante a pausa) | `M` |

### Identificação visual dos itens

Cada resíduo cai como um círculo colorido. A cor indica se ele é reciclável:

| Tipo | Cores |
|------|-------|
| **Reciclável** | Tons de verde e verde-água |
| **Não reciclável** | Tons de vermelho e laranja |

### Sistema de vidas

O jogador começa com **3 vidas**, exibidas como corações no canto superior esquerdo. Você perde uma vida quando:

- **Pega um item não reciclável** (acertou o errado) → mensagem "Isso não é reciclável!"
- **Deixa um item reciclável cair fora do coletor** (perdeu um que deveria pegar) → mensagem "Você deixou de reciclar!"

Deixar um item **não reciclável** cair é o comportamento esperado e **não pune**. Quando as vidas chegam a zero, a partida termina.

### Pontuação

| Tipo de item | Pontos por acerto |
|--------------|-------------------|
| Lento  | 100 |
| Médio  | 125 |
| Rápido | 150 |

Além dos pontos base, a cada acerto o jogador recebe um **bônus de tempo** proporcional aos segundos sobrevividos na partida (`+tempo_decorrido / 5` pontos). A cada 30 segundos sem perder, o jogo concede um **bônus de sobrevivência** de **+50 pontos**.

### Fim de jogo

Quando as vidas zeram, uma janela exibe a pontuação final e o tempo total no formato `mm:ss,cc`. Em seguida, o jogo retorna automaticamente ao menu e atualiza o **recorde** caso a pontuação tenha superado a anterior.

> ⚠️ O recorde é mantido **apenas durante a sessão atual** — ao fechar e reabrir o jogo, ele zera. (Persistência em arquivo é uma evolução natural do projeto.)

---

## 🧩 Mecânicas do jogo

### Geração de itens

- Os itens caem do topo em posições horizontais aleatórias.
- Existem no máximo **4 itens em tela** simultaneamente.
- A cada item gerado, a chance de ser reciclável ou não é de **50%**.
- A velocidade do item é sorteada com as seguintes probabilidades:

| Velocidade | Chance | Velocidade base (px/tick) |
|-----------|--------|---------------------------|
| Lenta  | 50% | 3 |
| Média  | 35% | 5 |
| Rápida | 15% | 7 |

### Dificuldade progressiva

A cada **30 segundos** de partida:

- A **velocidade global** de queda aumenta em 0,5× (até o teto de **3×**).
- O **intervalo entre spawns** diminui em 100 ms (até o mínimo de **600 ms**).
- O jogador recebe **+50 pontos** automaticamente.

### Tela de menu

A tela inicial mostra:

- O título do jogo.
- A **última pontuação** obtida na sessão.
- O **recorde** da sessão (em dourado).
- Botão **Jogar** — inicia uma nova partida.
- Botão **Sair** — encerra o programa.

---

## 🧱 Estrutura do projeto

```
recyclash/
└── src/
    ├── module-info.java
    └── RecyClash/
        ├── RecyClash.java   // JFrame principal + main()
        ├── MenuPanel.java   // tela de menu
        ├── GamePanel.java   // loop do jogo
        └── Trash.java       // item + enum SpeedType
```

### Classes

- **`RecyClash`** (`JFrame`): janela principal (800×600), configura `CardLayout` e alterna entre menu e jogo.
- **`MenuPanel`** (`JPanel`): tela inicial com botões "Jogar" e "Sair", exibe última pontuação e recorde.
- **`GamePanel`** (`JPanel` + `ActionListener`, `KeyListener`, `MouseMotionListener`): loop do jogo (`Timer` de 20 ms), render via `Graphics2D` com fundo em gradiente, controles, sistema de dificuldade, pausa e fim de jogo.
- **`Trash`**: item que cai. Tem posição, cor (definida em `determineColor`), `move(double)` aplicando o multiplicador de velocidade e `getBounds()` para colisão.
- **`Trash.SpeedType`**: enum aninhado com os valores `SLOW`, `MEDIUM`, `FAST`.

---

## ⚙️ Requisitos

- **JDK 18 ou superior**
- Sistema com suporte gráfico (Swing — não roda em ambiente *headless*).

---

## 🚀 Como compilar e executar

A partir da raiz do projeto:

```bash
# Compilar todos os fontes
mkdir -p bin
find src -name "*.java" > sources.txt
javac --release 18 -d bin @sources.txt

# Executar
java --module-path bin -m RecyClashAPS/RecyClash.RecyClash
```

Ou, no **Eclipse**, importe a pasta como projeto Java existente e execute a classe `RecyClash`.

---

## 🛠️ Tecnologias

- **Java SE 18+** — linguagem
- **Java Swing / AWT** — interface gráfica (`JFrame`, `JPanel`, `Graphics2D`, `CardLayout`, `Timer`)
- **JPMS** — módulo `RecyClashAPS` (`requires java.desktop`)

---

## 💡 Evoluções possíveis

- Persistir o recorde em arquivo (JSON, properties ou serialização).
- Adicionar trilha sonora e efeitos sonoros.
- Diferenciar tipos de reciclável (papel, plástico, vidro, metal) com lixeiras específicas.
- Implementar um modo educativo com informações sobre cada material.
- Adicionar conquistas e desafios diários.

---

## 📜 Licença

Projeto acadêmico (APS). Sinta-se livre para estudar e adaptar.
