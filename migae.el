;; override standard C-xC-s binding to call custom save-buffer
;; function that saves buffer in place and then copies to the
;; appropriate WEB-INF subdirectory.
;;
;; Installation:
;;   1.  edit .dir-locals.el and place it in <proj>/src
;;   2.  put this file (migae.el) in your emacs load path
;;   3.  add this to your init file:
;; (load (expand-file-name "~/.emacs.d/elisp/migae"))
;; (add-hook 'clojure-mode-hook
;;              (lambda ()
;;                (define-key clojure-mode-map "\C-xC-s"
;;                            'migae-save-buffer)))
;; repeat for js-mode-hook, nxml-mode-hook, and html-mode-hook.
;; copy and edit for other file types.

(defvar repl-url "http://localhost:8080/repl")

(defun migae-save-clj-buffer (&optional args)
  (interactive "p")
  (save-buffer)
  (message "running migae-save-clj-buffer")
  (if (assoc 'cljsrc file-local-variables-alist)
      (progn
	(let* ((cljsrc (file-name-as-directory
			 (substitute-in-file-name (cdr (assoc 'cljsrc file-local-variables-alist)))))
	     (cljdest (file-name-as-directory
		       (substitute-in-file-name (cdr (assoc 'cljdest file-local-variables-alist)))))
	     (srcfile (buffer-file-name))
	     (relname (file-relative-name srcfile cljsrc))
	     (tgtfile (concat cljdest "/classes/" relname)))
	(message "\ncljsrc %s" cljsrc)
	(message "srcfile %s" srcfile)
	(message "relname %s" relname)
	(message "cljdest %s" cljdest)
	(message "tggtfile %s" tgtfile)
	(message "copying %s\nto %s" srcfile tgtfile)
	(make-directory (file-name-directory tgtfile) t)
	(copy-file srcfile tgtfile t)))))

(defun migae-save-js-buffer (&optional args)
  (interactive "p")
  (save-buffer)
  (if (assoc 'jssrc file-local-variables-alist)
      (let* ((jssrc (file-name-as-directory (substitute-in-file-name (cdr (assoc 'jssrc file-local-variables-alist)))))
	     (jsdest (file-name-as-directory (substitute-in-file-name (cdr (assoc 'jsdest file-local-variables-alist)))))
	     (srcfile (buffer-file-name))
	     (relname (file-relative-name srcfile jssrc))
	     (tgtfile (concat jsdest relname)))
	(message "copying %s\nto %s" srcfile tgtfile)
	(make-directory (file-name-directory tgtfile) t)
	(copy-file srcfile tgtfile t))))

(defun migae-save-nxml-buffer (&optional args)
  (interactive "p")
  (save-buffer)
  (if (assoc 'nxmlsrc file-local-variables-alist)
      (let* ((nxmlsrc (file-name-as-directory (substitute-in-file-name (cdr (assoc 'nxmlsrc file-local-variables-alist)))))
	     (nxmldest (file-name-as-directory (substitute-in-file-name (cdr (assoc 'nxmldest file-local-variables-alist)))))
	     (srcfile (buffer-file-name))
	     (relname (file-relative-name srcfile nxmlsrc))
	     (tgtfile (concat nxmldest relname)))
	;; (message "nxmlsrc %s\nreltest %s" nxmlsrc (file-relative-name srcfile nxmlsrc))
	(message "copying %s to %s" srcfile tgtfile)
	(make-directory (file-name-directory tgtfile) t)
	(copy-file srcfile tgtfile t))))

(defun migae-save-html-buffer (&optional args)
  (interactive "p")
  (save-buffer)
  (if (assoc 'htmlsrc file-local-variables-alist)
      (let* ((htmlsrc (file-name-as-directory (substitute-in-file-name (cdr (assoc 'htmlsrc file-local-variables-alist)))))
	     (htmldest (file-name-as-directory (substitute-in-file-name (cdr (assoc 'htmldest file-local-variables-alist)))))
	     (srcfile (buffer-file-name))
	     (relname (file-relative-name srcfile htmlsrc))
	     (tgtfile (concat htmldest relname)))
	(message "htmlsrc %s\nreltest %s" htmlsrc (file-relative-name srcfile htmlsrc))
	(message "copying %s to %s" srcfile tgtfile)
	(make-directory (file-name-directory tgtfile) t)
	(copy-file srcfile tgtfile t))))


(provide 'migae)
