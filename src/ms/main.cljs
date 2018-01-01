(ns ms.main
  (:require [rum.core :as rum]
            [ms.ui :as ui]))

(enable-console-print!)

(rum/mount (ui/app-wrapper) (js/document.querySelector "#root"))
