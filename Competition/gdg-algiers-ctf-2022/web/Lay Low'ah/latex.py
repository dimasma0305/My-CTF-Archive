from pylatexenc.latex2text import LatexNodes2Text

latex = r"""
\textbf{Hi there!} Here is \emph{an equation}:
\begin{equation}
    \zeta = x + i y
\end{equation}
where $i$ is the imaginary unit.
"""
print(LatexNodes2Text().latex_to_text(latex))