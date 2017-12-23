package clientsfactoryjsh;

import Errors.MyExceptionOfCommandPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandPackage {

    private String command;
    private List<String> arguments;

    static List<String> Convert(String _string) {
        List<String> _arguments = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(_string, "-");
        while (tokenizer.hasMoreTokens()) {
            String result = tokenizer.nextToken();
            if (result.trim().length() != 0)
                _arguments.add(result.trim());
        }
        return _arguments;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getString() {
        String resultString = command;
        for (String s : arguments) {
            resultString = resultString + " -" + s;
        }
        return resultString;
    }

    public CommandPackage(String _string) throws MyExceptionOfCommandPackage {

        //удаляем комментарии
        if (_string.indexOf("//") >=0){
            String str = _string.substring(_string.indexOf("//")+0);
            _string = _string.replace(str,"");
        }


        if (_string.trim().length() == 0) {
            throw new MyExceptionOfCommandPackage("ERROR: An attempt to create a command from an empty string", 0);
        }
        List<String> l;
        l = Convert(_string.toLowerCase());
        command = l.get(0);
        l.remove(0);
        arguments = l;
    }


}

