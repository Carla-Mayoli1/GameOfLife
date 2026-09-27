import java.util.Random;

public class GameOfLife {

  public static void main(String[] args) {

    int width = 2;
    int height = 2;
    int generations = -2;
    int speed = 2;
    String population = "No Presente";
    int neighborhood = 3;
    int x = -2;

    int[] gameParameters = {width, height, generations, speed, neighborhood, x};
    String[] populationConfig = {population};

    processCommandLineArguments(args, gameParameters, populationConfig);

    width = gameParameters[0];
    height = gameParameters[1];
    generations = gameParameters[2];
    speed = gameParameters[3];
    neighborhood = gameParameters[4];
    population = populationConfig[0];
    x = gameParameters[5];

    printGameSettings(width, height, generations, speed, population, neighborhood);
    startGame(population, width, height, speed, generations, neighborhood, x);
  }


  // Procesa los argumentos de la línea de comandos y asigna valores a los parámetros
  private static void processCommandLineArguments(String[] args, int[] gameParameters,
      String[] populationConfig) {
    for (String argumentString : args) {
      int positionIndexEqual = findEqualSignIndex(argumentString);
      if (positionIndexEqual == -1) {
        continue;
      }

      String parameterKey = extractKeyOfArg(argumentString, positionIndexEqual);
      String parameterValue = extractValueOfArg(argumentString, positionIndexEqual);

      switch (parameterKey) {
        case "w":
          gameParameters[0] = validateIntegerParameterAssign(parameterValue,
              new String[]{"10", "20", "40", "80"}, 1);
          break;
        case "h":
          gameParameters[1] = validateIntegerParameterAssign(parameterValue,
              new String[]{"10", "20", "40"},
              1);
          break;
        case "g":
          gameParameters[2] = assignValidIntegerParameter(parameterValue, 0, Integer.MAX_VALUE, -1);
          break;

        case "s":
          gameParameters[3] = assignValidIntegerParameter(parameterValue, 250, 1000, 1);
          break;
        case "p":
          if (parameterValue.equals("rnd")) {
            populationConfig[0] = generateRandomPopulation(gameParameters[0], gameParameters[1]);
          } else {
            populationConfig[0] = parameterValue;
          }
          break;
        case "n":
          gameParameters[4] = assignValidIntegerParameter(parameterValue, 1, 5, -1);
          break;
        case "X":
          gameParameters[5] = assignValidIntegerParameter(parameterValue, 1, Integer.MAX_VALUE, -1);
          break;
        default:
          break;
      }
    }
  }

  // Asigna un valor a un parámetro si es válido, o usa el valor por defecto si no lo es.
  private static int validateIntegerParameterAssign(String parameterValue, String[] validValues,
      int defaultValue) {
    int validatedParameter = defaultValue;

    for (String validValue : validValues) {
      if (parameterValue.equals(validValue)) {
        validatedParameter = convertToInteger(parameterValue);
      }
    }
    return validatedParameter;
  }

  // Asigna un valor entero válido dentro de un rango, o usa el valor por defecto si no es válido.
  private static int assignValidIntegerParameter(String parameterValue, int minimumValue,
      int maximumValue, int defaultValue) {
    int validatedParameter = defaultValue;
    if (isInteger(parameterValue)) {
      int value = convertToInteger(parameterValue);
      if (value >= minimumValue && value <= maximumValue) {
        validatedParameter = value;
      }
    }
    return validatedParameter;
  }


  // Inicia el juego verificando la validez de la población y ejecutando las generaciones
  private static void startGame(String population, int width, int height, int speed,
      int generations, int neighborhood, int x) {

    if (isValidPopulation(population)) {
      char[][] gameOfLifeBoard = transformPopulationToArray(population);
      boolean populationFitsInGrid = verifyBoardDimensions(width, height, gameOfLifeBoard);

      if (populationFitsInGrid) {
        if (speed != 1 && speed != 2 && neighborhood != -1 && generations != -1
            && generations != -2) {
          runGenerations(gameOfLifeBoard, speed, generations, neighborhood,x);
        } else {
          drawGameOfLifeGeneration(gameOfLifeBoard);
        }
      }
    }
  }

  // Ejecuta las generaciones del Juego de la Vida
  private static void runGenerations(char[][] gameOfLifeBoard, int speed, int generations, int neighborhood, int x) {
    for (int gen = 0; gen < generations || generations == 0; gen++) {
      System.out.println("\nGeneration " + (gen + 1) + ":");
      if (gen == x - 1) {
        char[][] board = new char[gameOfLifeBoard.length][gameOfLifeBoard[0].length];
        drawGameOfLifeGeneration(board);
      } else {
        drawGameOfLifeGeneration(gameOfLifeBoard);
      }

      waitFor(speed);

      gameOfLifeBoard = nextGeneration(gameOfLifeBoard, neighborhood);
    }
  }


  // Pausa la ejecución por un tiempo determinado
  private static void waitFor(int speed) {
    try {
      Thread.sleep(speed);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  // Calcula la siguiente generación del tablero
  private static char[][] nextGeneration(char[][] gameOfLifeboard, int neighborhood) {
    int rows = gameOfLifeboard.length;
    int cols = gameOfLifeboard[0].length;
    char[][] nextGenerationBoard = new char[rows][cols];

    for (int x = 0; x < rows; x++) {
      for (int y = 0; y < cols; y++) {
        int aliveCount = countAliveNeighbors(x, y, gameOfLifeboard, neighborhood);
        nextGenerationBoard[x][y] = applyGameOfLifeRules(gameOfLifeboard[x][y], aliveCount);
      }
    }

    return nextGenerationBoard;
  }

  // Aplica las reglas a una celda
  private static char applyGameOfLifeRules(char cellState, int livingNeighborsCount) {
    char nextCellState = '0';

    if (cellState == '1') {
      if (livingNeighborsCount >= 2 && livingNeighborsCount <= 3) {
        nextCellState = '1';
      }
    } else {
      if (livingNeighborsCount == 3) {
        nextCellState = '1';
      }
    }

    return nextCellState;
  }

  // Cuenta los vecinos vivos de una celda
  private static int countAliveNeighbors(int x, int y, char[][] gameOfLifeboard, int neighborhood) {
    int[][] neighbors = getNeighbors(x, y, gameOfLifeboard, neighborhood);
    int aliveCount = 0;

    for (int i = 0; i < neighbors.length; i++) {
      int neighborX = neighbors[i][0];
      int neighborY = neighbors[i][1];
      if (gameOfLifeboard[neighborX][neighborY] == '1') {
        aliveCount++;
      }
    }
    return aliveCount;
  }

  private static int[][] getNeighbors(int x, int y, char[][] gameOfLifeboard, int neighborhood) {
    int[][] neighbors;
    switch (neighborhood) {
      case 1:
        neighbors = getCrossNeighbors(x, y, gameOfLifeboard);
        break;
      case 2:
        neighbors = getExtendedCrossNeighbors(x, y, gameOfLifeboard);
        break;
      case 3:
        neighbors = getMooreNeighbors(x, y, gameOfLifeboard);
        break;
      case 4:
        neighbors = getDiagonalNeighbors(x, y, gameOfLifeboard);
        break;
      case 5:
        neighbors = getCustomNeighbors(x, y, gameOfLifeboard);
        break;
      default:
        neighbors = getMooreNeighbors(x, y, gameOfLifeboard);
        break;
    }
    return neighbors;
  }


  // Vecindario 1: Cruz (Solo ortogonales) Jala University
  private static int[][] getCrossNeighbors(int x, int y, char[][] gameOfLifeBoard) {
    int[][] crossOffsets = {
        {-1, 0}, {1, 0},
        {0, -1}, {0, 1}
    };
    return getValidNeighbors(x, y, crossOffsets, gameOfLifeBoard);
  }

  // Vecindario 2: cruz con los lados izquierda pintado de arriba y derecha de abajo Programming 1
  private static int[][] getExtendedCrossNeighbors(int x, int y, char[][] gameOfLifeBoard) {
    int[][] extendedCrossOffsets = {
        {-1, -1}, {-1, 0},
        {0, -1}, {0, 1},
        {1, 0}, {1, 1}
    };

    return getValidNeighbors(x, y, extendedCrossOffsets, gameOfLifeBoard);
  }


  // Vecindario 3: Moore (Completo)
  private static int[][] getMooreNeighbors(int x, int y, char[][] gameOfLifeBoard) {
    int[][] mooreOffsets = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1}, {0, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    return getValidNeighbors(x, y, mooreOffsets, gameOfLifeBoard);
  }

  // Vecindario 4: reverse Jala University
  private static int[][] getDiagonalNeighbors(int x, int y, char[][] gameOfLifeBoard) {
    int[][] diagonalOffsets = {
        {-1, -1}, {-1, 1},
        {1, -1}, {1, 1}
    };

    return getValidNeighbors(x, y, diagonalOffsets, gameOfLifeBoard);
  }

  // Vecindario 5: Custom
  private static int[][] getCustomNeighbors(int x, int y, char[][] gameOfLifeBoard) {
    int[][] relativeOffsets = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {1, -1}, {1, 0}, {1, 1}
    };

    return getValidNeighbors(x, y, relativeOffsets, gameOfLifeBoard);
  }

  // Obtiene los vecinos validos
  private static int[][] getValidNeighbors(int x, int y, int[][] directionOffsets,
      char[][] gameOfLifeBoard) {
    int[][] validNeighbors = new int[directionOffsets.length][2];
    int validNeighborCount = 0;
    for (int i = 0; i < directionOffsets.length; i++) {
      int neighborX = x + directionOffsets[i][0];
      int neighborY = y + directionOffsets[i][1];

      if (neighborX >= 0 && neighborX < gameOfLifeBoard.length && neighborY >= 0
          && neighborY < gameOfLifeBoard[0].length) {
        validNeighbors[validNeighborCount][0] = neighborX;
        validNeighbors[validNeighborCount][1] = neighborY;
        validNeighborCount++;
      }
    }
    return trimNeighborArray(validNeighbors, validNeighborCount);
  }

  // Recorta el array de vecinos válidos según el número de vecinos válidos.
  private static int[][] trimNeighborArray(int[][] validNeighbors, int validNeighborCount) {
    int[][] croppedMatrix = new int[validNeighborCount][2];
    for (int i = 0; i < validNeighborCount; i++) {
      croppedMatrix[i][0] = validNeighbors[i][0];
      croppedMatrix[i][1] = validNeighbors[i][1];
    }
    return croppedMatrix;
  }

  // Verifica que las dimensiones de la grilla y la población sean válidas.
  private static boolean verifyBoardDimensions(int width, int height, char[][] gameOfLifeBoard) {
    boolean isGridSizeValid = true;

    if (width == 1 || width == 2 || height == 1 || height == 2) {
      System.out.println("[Error] Grid dimensions are invalid.");
      isGridSizeValid = false;
    }

    if (isGridSizeValid) {
      if (gameOfLifeBoard.length > height || gameOfLifeBoard[0].length > width) {
        System.out.println("[Error] The population entered exceeds the grid size.");
        isGridSizeValid = false;
      }
    }

    return isGridSizeValid;
  }


  // Imprime los valores de las variables de configuración del juego.
  private static void printGameSettings(int width, int height, int generations, int speed,
      String population, int neighborhood) {
    System.out.println();

    printParameter("width", width, 2);
    printParameter("height", height, 2);

    if (generations == -1) {
      System.out.println("generations = [Invalido]");
    } else if (generations == -2) {
      System.out.println("generations = [No Presente]");
    } else {
      System.out.println("generations = " + "[" + generations + "]");
    }
    printParameter("speed", speed, 2);

    if (population.equals("No Presente")) {
      System.out.println("population = [No presente]");
    } else if (isValidPopulation(population)) {
      System.out.println("population = [\"" + population + "\"]");
    } else {
      System.out.println("population = [Invalido]");
    }

    if (neighborhood == -1) {
      System.out.println("vecindario = [Invalido]");
    } else {
      System.out.println("vecindario = " + neighborhood);
    }

  }

  private static void printParameter(String parameterName, int parameterValue,
      int notPresentValue) {
    String statusParameter;

    if (parameterValue == 1) {
      statusParameter = "[Invalido]";
    } else if (parameterValue == notPresentValue) {
      statusParameter = "[No presente]";
    } else {
      statusParameter = "[" + parameterValue + "]";
    }
    System.out.println(parameterName + " = " + statusParameter);
  }


  // Encuentra la posición del '=' en la cadena del argumento
  private static int findEqualSignIndex(String argumentString) {
    int equalSignIndex = -1;

    for (int i = 0; i < argumentString.length(); i++) {
      if (argumentString.charAt(i) == '=') {
        equalSignIndex = i;
      }
    }

    return equalSignIndex;
  }

  //Extrae la clave w=10,h,s,g,p y convertirlo en cadena
  private static String extractKeyOfArg(String argumentString, int positionIndexEqual) {
    char[] keyArray = new char[positionIndexEqual];

    for (int i = 0; i < positionIndexEqual; i++) {
      keyArray[i] = argumentString.charAt(i);
    }
    String extractedKey = "";
    for (int i = 0; i < positionIndexEqual; i++) {
      extractedKey += keyArray[i];
    }
    return extractedKey;
  }

  // Extrae el valor después del '='
  private static String extractValueOfArg(String argumentString, int positionIndexEqual) {
    int valueLength = argumentString.length() - (positionIndexEqual + 1);
    char[] valueArray = new char[valueLength];

    for (int i = 0; i < valueLength; i++) {
      valueArray[i] = argumentString.charAt(positionIndexEqual + 1 + i);
    }
    String extractedValue = "";
    for (int i = 0; i < valueLength; i++) {
      extractedValue += valueArray[i];
    }
    return extractedValue;
  }

  // Verifica si un valor es un número entero
  private static boolean isInteger(String value) {
    boolean isInteger;
    try {
      Integer.parseInt(value);
      isInteger = true;
    } catch (Exception e) {
      isInteger = false;
    }
    return isInteger;
  }

  // Convierte un valor a entero si es posible
  private static int convertToInteger(String value) {
    int convertedValue = 0;
    try {
      convertedValue = Integer.parseInt(value);
    } catch (Exception e) {
      System.out.println("Mistake");
    }
    return convertedValue;
  }

  //Transformar la cadena a matriz
  public static char[][] transformPopulationToArray(String setupText) {
    int rowsCount = countRowsInSetup(setupText);
    int maxColumns = getMaxColumns(setupText);

    char[][] gameOflifeBoard = new char[rowsCount][maxColumns];

    fillGameBoardWithSetup(gameOflifeBoard, setupText);
    replaceEmptySpacesWithZero(gameOflifeBoard);

    return gameOflifeBoard;
  }

  // Metodo para contar el número de fila
  private static int countRowsInSetup(String setupText) {
    int rowsCount = 1;
    for (int i = 0; i < setupText.length(); i++) {
      if (setupText.charAt(i) == '#') {
        rowsCount++;
      }
    }
    return rowsCount;
  }

  //Metodo para obtener el numero maximo de columnas
  private static int getMaxColumns(String setupText) {
    int maxColumns = 0;
    int currentRowLength = 0;

    for (int i = 0; i < setupText.length(); i++) {
      if (setupText.charAt(i) == '#') {
        if (currentRowLength > maxColumns) {
          maxColumns = currentRowLength;
        }
        currentRowLength = 0;
      } else {
        currentRowLength++;
      }
    }

    if (currentRowLength > maxColumns) {
      maxColumns = currentRowLength;
    }

    return maxColumns;
  }


  //Metodo para llenar la matriz con la cadena
  private static void fillGameBoardWithSetup(char[][] gameOflifeBoard, String setupText) {
    int currentRow = 0;
    int currentRowLength = 0;

    for (int i = 0; i < setupText.length(); i++) {
      if (setupText.charAt(i) == '#') {
        currentRow++;
        currentRowLength = 0;
      } else {
        if (setupText.charAt(i) == '1' || setupText.charAt(i) == '0') {
          gameOflifeBoard[currentRow][currentRowLength] = setupText.charAt(i);
        }
        currentRowLength++;
      }
    }
  }

  //Metodo para reemplazar los espacios vacios con '0'
  private static void replaceEmptySpacesWithZero(char[][] gameOflifeBoard) {
    for (int i = 0; i < gameOflifeBoard.length; i++) {
      for (int j = 0; j < gameOflifeBoard[i].length; j++) {
        if (gameOflifeBoard[i][j] == '\u0000') {
          gameOflifeBoard[i][j] = '0';
        }
      }
    }
  }

  // Dibuja la generacion en el tablero
  public static void drawGameOfLifeGeneration(char[][] gameofLifeGeneration) {
    for (int i = 0; i < gameofLifeGeneration.length; i++) {
      for (int j = 0; j < gameofLifeGeneration[i].length; j++) {
        System.out.print(gameofLifeGeneration[i][j]);
        if (j < gameofLifeGeneration[i].length - 1) {
          System.out.print("     ");
        }
      }
      System.out.println();
    }
  }

  // Verifica si la población es correcta y solo contiene "0" y "1"
  private static boolean isValidPopulation(String population) {
    boolean isValid = true;

    if (population.length() == 0 || population.equals("No Presente")) {
      isValid = false;
    } else {
      for (int i = 0; i < population.length(); i++) {
        char currentChar = population.charAt(i);
        if ((currentChar != '0' && currentChar != '1') && currentChar != '#') {
          isValid = false;
        }
      }
    }

    return isValid;
  }


  // Generar una poblacion aletoria
  private static String generateRandomPopulation(int width, int height) {

    StringBuilder populationBuilder = new StringBuilder();
    Random random = new Random();

    if (width < 10) {
      width = 10;
    }
    if (height < 10) {
      height = 10;
    }

    int randomHeight = random.nextInt(height) + 1;
    int randomWidth = random.nextInt(width) + 1;

    for (int i = 0; i < randomHeight; i++) {
      for (int j = 0; j < randomWidth; j++) {
        populationBuilder.append(random.nextInt(2));
      }
      if (i < randomHeight - 1) {
        populationBuilder.append("#");
      }
    }
    return populationBuilder.toString();
  }
}