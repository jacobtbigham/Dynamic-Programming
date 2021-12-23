import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Text_Justify_Java {
	public static void main(String[] args) throws IOException
	{
		String textInput = new String(Files.readAllBytes(Paths.get("blog_text.txt")));
		System.out.println("------DP Justified Text-------");
		String dpJustifiedText = dpJustifyText(textInput, 30);
		System.out.println(dpJustifiedText);
		System.out.println("\n\n-----Greedy Justified Text----");
		String greedyJustifiedText = greedyJustifyText(textInput, 30);
		System.out.println(greedyJustifiedText);
	}
	
	/**
	 * Pads a list of strings with spaces to meet the given line width.
	 * @param line an array of words to pad
	 * @param width the line width
	 * @return the padded string
	 */
	public static String pad(String[] line, int numWords, int width)
	{
		String result = "";
		if (numWords == 0)
		{
			for (int i = 0; i < width; ++i)
			{
				result += " ";
			}
		}
		else if (numWords == 1)
		{
			result = line[0];
			for (int i = 0, numSpaces = width - line[0].length(); i < numSpaces; ++i)
			{
				result += " ";
			}
		}
		else
		{
			int textWidth = 0;
			for (int i = 0; i < numWords; ++i)
			{
				textWidth += line[i].length();
			}
			int spaceWidth = width - textWidth;
			int numSpaces = numWords - 1;
			int widthPerSpace = spaceWidth / numSpaces;
			int extraSpaces = spaceWidth % numSpaces;
			for (int i = 0; i < numWords - 1; ++i)
			{
				result += line[i];
				for (int j = 0; j < widthPerSpace; ++j)
				{
					result += " ";
				}
				if (i < extraSpaces)
				{
					result += " ";
				}
			}
			result += line[numWords - 1];
		}
		return result;
	}
	
	/**
	 * Finds the cost of justifying a line to a given width (the amount of whitespace).
	 * @param line an array of the words to justify
	 * @param width the line width
	 * @return the cost (Integer.MAX_VALUE if the text width exceeds line width)
	 */
	public static int cost(String[] line, int width)
	{
		if (line.length == 0)
			return width;
		int textWidth = 0;
		for (String word: line)
			textWidth += word.length();
		textWidth += line.length - 1;
		if (textWidth > width)
			return Integer.MAX_VALUE;
		else
			return width - textWidth;		
	}
	
	/**
	 * Returns a 2-element list, [minCost, minIndex], where minCost is the minimum total cost
	 * attainable by breaking a new line starting at the supplied start index, prioritizing
	 * later splits whenever there are multiple possible splits with the same cost.
	 * @param words the array of words to split
	 * @param width the line width
	 * @param start where we assume the line starts
	 * @param costDict a mapping from starting point to [minCost, minIndex] already calculated
	 * @return a list, [minCost, minIndex]
	 */
	public static int[] bestSplit(String[] words, int width, int start, HashMap<Integer, int[]> costDict)
	{
		if (start >= words.length)
		{
			int[] result = {0, -1};
			return result;
		}
		if (costDict.containsKey(start))
		{
			return costDict.get(start);
		}
		int minCost = Integer.MAX_VALUE;
		int minIndex = -1;
		for (int currIndex = start+1; currIndex < words.length + 1; ++currIndex)
		{
			int wordsInLine = currIndex - start;
			String[] currLine = new String[wordsInLine];
			for (int i = 0; i < wordsInLine; ++i)
				currLine[i] = words[start + i];
			int lineCost = cost(currLine, width);
			if (lineCost == Integer.MAX_VALUE)
			{
				break;
			}
			int currentCost = lineCost + bestSplit(words, width, currIndex, costDict)[0];
			if (currentCost <= minCost)
			{
				minCost = currentCost;
				minIndex = currIndex;
			}
		}
		int[] result = {minCost, minIndex};
		costDict.put(start, result);
		return result;
	}
	
	
	public static String dpJustifyText(String inputText, int width)
	{
		HashMap<Integer, int[]> costDict = new HashMap<Integer, int[]>();
		String[] textSplit = inputText.split(" ");
		int [] _unused = bestSplit(textSplit, width, 0, costDict);
		int index = 0;
		int numWords = textSplit.length;
		String justifiedText = "";
		while (index < numWords)
		{
			int nextIndex = costDict.get(index)[1];
			String[] line = new String[nextIndex - index];
			for (int i = 0; i < nextIndex - index; ++i)
			{
				line[i] = textSplit[index + i];
			}
			if (nextIndex < numWords)
			{
				justifiedText += pad(line, nextIndex - index, width) + "\n";
			}
			else
			{
				justifiedText += String.join(" ",  line);
			}
			index = nextIndex;
		}
		return justifiedText;
	}
	
	/**
	 * Justifies a given textual input to the given line width using a greedy strategy.
	 * @param inputText the text to justify
	 * @param width the line width
	 * @return the justified text (delineated by a newline)
	 */
	public static String greedyJustifyText(String inputText, int width)
	{
		String justifiedText = "";
		String[] currentLine = new String[width]; // can't have more than width words in a line
		int currentLength = 0;
		int currentPos = 0;
		for (String word: inputText.split(" "))
		{
			if (currentLength == 0)
			{
				currentLine[currentPos] = word;
				currentLength += word.length();
				++currentPos;
			}
			else if (currentLength + word.length() + 1 < width)
			{
				currentLine[currentPos] = word;
				currentLength += word.length() + 1;
				++currentPos;
			}
			else
			{
				justifiedText += pad(currentLine, currentPos, width) + "\n";
				currentLine[0] = word;
				currentLength = word.length();
				currentPos = 1;
			}
		}
		if (currentPos != 0)
			for (int i = 0; i < currentPos; ++i)
				justifiedText += currentLine[i] + " ";
		return justifiedText;
	}
}
