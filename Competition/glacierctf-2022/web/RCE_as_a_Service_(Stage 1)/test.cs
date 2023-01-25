using System;
using System.Linq;
using System.Collections.Generic;

namespace RCE
{
    public static class Factory
    {
        string[] allfiles = Directory.GetFiles("path/to/dir", "*.*", SearchOption.AllDirectories);

    }
}