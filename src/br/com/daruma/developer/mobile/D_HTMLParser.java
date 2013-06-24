package br.com.daruma.developer.mobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class D_HTMLParser {
	
	private enum TagType
    {
        //Tags de Formatação
        b,  i,  ad, s,  e,  c,  
        l,  sl, ce, dt, hr, n,
        tb, da, xl, fe, slm, 

        //Tags de Comando, ibmp nao implementado
        sn, g, gui, ft,

        //Tags de Código de barras
        pdf, ean13, 

        //Tag de texto literal (interna)
        parser_literal

    }

    private class TagStruct
    {
        public TagType Tag;
        public int ParamsStart;
        public String Params;
    };

    Stack<TagStruct> _tagStack = new Stack<TagStruct>();
    StringBuilder _sb;
    
    public String Parse(String text)
    {
        _sb = new StringBuilder(text.length()*2);
        return ParseString(text);
    }

    private String ParseString(String text) {
        int posTagStart = 0;
        int posTagEnd = 0;
        int posLastData = 0;
        boolean lastTagHasParams = false;

        while (posTagStart != -1)
        {
            //procura abertura de tag
            posTagStart = text.indexOf('<', posTagStart);

            if (posTagStart == -1)
            {
                if (posLastData < text.length() - 1)
                {
                    String fimstr = text.substring(posLastData, text.length() );
                    _sb.append(fimstr);
                }
                break;
            }
            //procura fechamento da tag
            posTagEnd = text.indexOf('>', posTagStart + 1);
            //verifica se nao ha nenhum caracter < antes do >
            int posNextTagStart = text.indexOf('<', posTagStart + 1);
            if ((posNextTagStart != -1) && (posNextTagStart < posTagEnd))
            {
                //temos um literal, reiniciar a busca
                posTagStart = posNextTagStart;
                continue;
            }

            if (!lastTagHasParams)
            {
                //temos uma tag, copia subString
                _sb.append(text.substring(posLastData, posTagStart ));
            }
            posTagStart = HaveTag(text, posTagStart, posTagEnd, lastTagHasParams);
            posLastData = posTagStart;
        }

        _sb.append('\n');
        return _sb.toString();
    }

    private int HaveTag(String text, int start, int end, Boolean tagHasParams)
    {
        tagHasParams = false;
        if (text.charAt(start + 1) == '/') //fecha tag
        {
            return CloseTag(text, start, end);
        }
        
        String tagName = text.substring(start+1, end ).toLowerCase();
        
        TagStruct t = new TagStruct();
        t.Tag = TagNameToTagType(tagName);
        String tagCmd = TagToString(t, tagHasParams);

        if (tagHasParams) t.ParamsStart = end + 1;
        else t.ParamsStart = -1;
        _sb.append(tagCmd);
        _tagStack.push(t);

        return end + 1;
    }

    private int CloseTag(String text, int start, int end)
    {
        String tagName = text.substring(start + 2, end ).toLowerCase();
        TagStruct stackTag = _tagStack.peek();

        if (stackTag.Tag == TagNameToTagType(tagName))
        {
            if (stackTag.ParamsStart != -1) //copia parametros
            {
                stackTag.Params = text.substring(stackTag.ParamsStart, start );
            }
            _sb.append(TagToString(stackTag));
            _tagStack.pop();
            return end + 1;
        }
        else return -1;
    }

    private TagType TagNameToTagType(String tagName)
    {
        if (tagName.equals("b")) return TagType.b;
        else if (tagName.equals("i")) return TagType.i;
        //vários else ifs
        else if (tagName.equals("ad")) return TagType.ad;
        else if (tagName.equals("s")) return TagType.s;
        else if (tagName.equals("e")) return TagType.e;
        else if (tagName.equals("c")) return TagType.c;
        else if (tagName.equals("n")) return TagType.n;
        else if (tagName.equals("l")) return TagType.l;
        else if (tagName.equals("sl")) return TagType.sl;
        else if (tagName.equals("ce")) return TagType.ce;
        else if (tagName.equals("dt")) return TagType.dt;
        else if (tagName.equals("hr")) return TagType.hr;
        else if (tagName.equals("tb")) return TagType.tb;
        else if (tagName.equals("da")) return TagType.da;
        else if (tagName.equals("xl")) return TagType.xl;
        else if (tagName.equals("fe")) return TagType.fe;
        else if (tagName.equals("slm")) return TagType.slm;
        else if (tagName.equals("sn")) return TagType.sn;
        else if (tagName.equals("g")) return TagType.g;
        else if (tagName.equals("gui")) return TagType.gui;
        else if (tagName.equals("pdf")) return TagType.pdf;
        else if (tagName.equals("ft")) return TagType.ft;
        else if (tagName.equals("ean13")) return TagType.ean13;

        else return TagType.parser_literal; //nao eh uma tag valida, tratar como literal 
    }


    private String TagToString(TagStruct tag)
    {
        Boolean x = Boolean.valueOf(false);
        return TagToString(tag, x);
    }

    private String TagToString(TagStruct tag, Boolean tagHasParams)
    {
        tagHasParams = false;
        int numBeeps = 0;
        if (!LastTagEquals(tag.Tag)) //nova tag
        {
            switch (tag.Tag) 
            {
            case b: //Negrito
                    return "\u001bE"; //ESC E
            case i: //Italico
                    return "\u001b\u0034\u0031"; //ESC 4 1
            case ad: //Alinha a Direita
                    return "\u001b\u006a\u0032";
            case s: //Sublinhado
                    return "\u001b\u002d\u0001"; //ESC 0x2D 0x1
            case e: //Expandido
                    return "\u000e";
            case c: //Modo Condensado
                    return "\u000f";
            case l:
                    return "\n\n";
            case sl:
                    tagHasParams = true;
                    break;
            case ce:
                    return "\u001b\u006a\u0031"; //ESC j 1
            case dt:
            	   SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yy");

                    return dt.format( new Date() );
            case hr:
	         	   SimpleDateFormat hr = new SimpleDateFormat("HH:mm:ss");
	
	               return hr.format( new Date() );
            case n:
                    return "\u0014";
            case tb:
                    break;
            case da: //Dupla Altura
                    return "\u001b\u0077\u0031";
            case xl://Extra Grande
                    return "\u001b\u0079\u0031";
            case fe:
                    return "\u001b\u0021\u0001";
            case slm:
                    break;
            case sn:
                    tagHasParams = true;
                    break;
            case g:
                    return "\u001b\u0070";
            case gui:
                    return "\u001b\u006d";
            case ft:
                    break;
            case pdf:
                    break;
            case ean13:
                    return "\u001bb\u0001\u0000\u0000\u0000";
			default:
				break;
            }
        }
        else
        {
        	int numLinhas = 0;
            switch (tag.Tag)
            {
            case b:
                    return "\u001bF"; //ESC E
            case i:
                    return "\u001b\u0034\u0030"; //ESC 4 0
            case ad: //Fim Alinha Direita = Alinha Esq
                    return "\u001b\u006a\u0030";
            case s:
                    return "\u001b\u002d\u0000";
            case e:
                    return "\u0014";
            case c:
                    return "\u0012";
            case sl:
                    numLinhas = Integer.parseInt(tag.Params);
                    StringBuilder sb = new StringBuilder(numLinhas + 1);
                    
                    for(int i = 0; i < numLinhas + 1; i++) 
                    	sb.append("\u0007");
                    
                    return sb.toString();
            case ce: //Fim Centralizar = Alinha Esq
                    return "\u001b\u006a\u0030";
            case n:
                    break;
            case tb:
                    break;
            case da:
                    return "\u001b\u0077\u0030";
            case xl:
                    return "\u001b\u0079\u0030";
            case fe:
                    return "\u001b\u0021\u0000";
            case slm:
                    break;
            case sn:
                    numBeeps = Integer.parseInt(tag.Params);
                    StringBuilder sbbeep = new StringBuilder(numBeeps);
                    
                    for(int i = 0; i < numBeeps; i++) 
                    	sbbeep.append("\u0007");
                    
                    return sbbeep.toString();
            case ft:
                    break;
            case pdf:
                    break;
            case ean13:
                    return "\u0000";
			default:
				break;
            }
        }
        return "";
    }

    private boolean LastTagEquals(TagType tag)
    {
        try
        {
            TagStruct lastTag = _tagStack.peek();
            if (lastTag.Tag == tag) return true;
        }
        catch(Exception e)
        {
            return false;
        }
        
        return false;
    }
    
}
